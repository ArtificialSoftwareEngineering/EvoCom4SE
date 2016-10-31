package scalabio

import javabio.optmodel.mappings.metaphor.MetaphorCode
import javabio.storage.entities.{RefKey, Register}
import javabio.storage.repositories.RegisterRepository

import edu.wayne.cs.severe.redress2.controller.MetricCalculator
import edu.wayne.cs.severe.redress2.entity.{AttributeDeclaration, MethodDeclaration, TypeDeclaration}
import edu.wayne.cs.severe.redress2.entity.refactoring.RefactoringOperation

import scala.collection.JavaConversions._
import scalabio.FitnessScalaApply._
import scala.concurrent.{Await, ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
  * Created by david on 6/10/16.
  */

object FitnessScalaApply{
  type Class = String
  type RefMetric = Map[String, ClassMap]
  type Metric = Map[String, Double]
  type ClassMap = Map[Class, Metric]

  object RefAcronym extends Enumeration{
    val EM, IM, RMMO, MF, PDF, PUF, MM, PDM, PUM, RDI, RID, EC = Value
  }

  case class RefactorRegister(operRef: RefactoringOperation,
                              src:Option[String], tgt:Option[String]=None,
                              fld:Option[String], mtd:Option[String])
}

trait FitnessCacheUtils{
  /**
    * Extract the Params from a "Extract Class" Refactoring
    *
    * @param operRef
    */
  private[scalabio] def extractParamsEC(operRef: RefactoringOperation):RefactorRegister = {
    //1.Extracting src from subrefs
    val src = (operRef.getSubRefs.toList flatMap  {oneRef =>
      oneRef.getParams.toMap.get("src") flatMap  { refactoringParameterList =>
        (refactoringParameterList.toList map {refactoringParameter =>
          refactoringParameter.getCodeObj.asInstanceOf[TypeDeclaration].getId.toString
        }).headOption
      }
    }).headOption

    //2.Extracting fld from subrefs
    val fld = (operRef.getSubRefs.toList flatMap {oneRef =>
      oneRef.getParams.toMap.get("fld") flatMap  { refactoringParameterList =>
        (refactoringParameterList.toList map { refactoringParameter =>
          refactoringParameter.getCodeObj.asInstanceOf[AttributeDeclaration].getObjName
        }).headOption
      }
    } ).headOption

    //3.Extracting mtd from subrefs
    val mtd = (operRef.getSubRefs.toList flatMap{oneRef =>
      oneRef.getParams.toMap.get("mtd") flatMap  { refactoringParameterList =>
        (refactoringParameterList.toList map {refactoringParameter =>
          refactoringParameter.getCodeObj.asInstanceOf[MethodDeclaration].getObjName
        }).headOption
      }
    } ).headOption

    RefactorRegister(operRef = operRef, src = src, fld = fld, mtd = mtd )
  }

  private[scalabio] def extractParams(operRef: RefactoringOperation):RefactorRegister = {
    //1.Extracting src from ref
    val src = operRef.getParams.toMap.get("src") flatMap  { refactoringParameterList =>
       (refactoringParameterList.toList map {refactoringParameter =>
          refactoringParameter.getCodeObj.asInstanceOf[TypeDeclaration].getId.toString
        }).headOption
      }

    val tgt = operRef.getParams.toMap.get("tgt") flatMap { refactoringParameterList =>
      (refactoringParameterList.toList map {refactoringParameter =>
        refactoringParameter.getCodeObj.asInstanceOf[TypeDeclaration].getId.toString
      }).headOption
    }

    //2.Extracting fld from ref
    val fld = operRef.getParams.toMap.get("fld") flatMap  { refactoringParameterList =>
      (refactoringParameterList.toList map {refactoringParameter =>
          refactoringParameter.getCodeObj.asInstanceOf[AttributeDeclaration].getObjName
        }).headOption
      }

    //3.Extracting mtd from ref
    val mtd = operRef.getParams.toMap.get("mtd") flatMap  { refactoringParameterList =>
      (refactoringParameterList.toList map {refactoringParameter =>
          refactoringParameter.getCodeObj.asInstanceOf[MethodDeclaration].getObjName
        }).headOption
      }

    RefactorRegister(operRef = operRef, src = src, tgt = tgt, fld = fld, mtd = mtd )
  }

  private def matchMetric(acronym: RefAcronym.Value , src:String, tgt:String, mtd:String, fld:String): RefKey ={
    acronym match {
      case RefAcronym.EM  =>
        new RefKey(acronym.toString , src, "", mtd, "")
      case RefAcronym.IM  =>
        new RefKey(acronym.toString, src, "", mtd, "")
      case RefAcronym.RMMO  =>
        new RefKey(acronym.toString, src, "", mtd, "")
      case RefAcronym.MF  =>
        new RefKey(acronym.toString, src, tgt, "", fld)
      case RefAcronym.PDF  =>
        new RefKey(acronym.toString, src, tgt, "", fld)
      case RefAcronym.PUF  =>
        new RefKey(acronym.toString, src, tgt, "", fld)
      case RefAcronym.MM  =>
        new RefKey(acronym.toString, src, tgt, mtd, "")
      case RefAcronym.PDM  =>
        new RefKey(acronym.toString, src, tgt, mtd, "")
      case RefAcronym.PUM  =>
        new RefKey(acronym.toString, src, tgt, mtd, "")
      case RefAcronym.RDI  =>
        new RefKey (acronym.toString, src, tgt, "", "")
      case RefAcronym.RID  =>
        new RefKey (acronym.toString, src, tgt, "", "")
      case RefAcronym.EC  =>
        new RefKey(acronym.toString, src, "", mtd, fld)
    }
  }

  private[scalabio] def retrieveMetrics(refactParam: RefactorRegister, acronym: RefAcronym.Value): ClassMap ={

    val refKeys = matchMetric(acronym = acronym, src = refactParam.src.getOrElse(""),
      tgt = refactParam.tgt.getOrElse(""),
      mtd = refactParam.mtd.getOrElse("-1"),
      fld = refactParam.fld.getOrElse("-1")
    )

    //Calling Cache
    val listRegister = MetaphorCode.RefactoringCache.get(refKeys).toList

    lazy val claMap = listRegister map  (_.getClasss) map { optRegClass =>
      val met = (listRegister find (_.getClasss == optRegClass ) map {sMetricL =>
        Map(sMetricL.getMetric -> sMetricL.getValue) }).getOrElse(Map.empty)
      optRegClass -> met
    } toMap

    lazy val clasMap:ClassMap = (listRegister map {optRegClass =>
      optRegClass.getClasss -> Map(optRegClass.getMetric -> optRegClass.getValue)
    }).toMap[String, Map[String, Double]]

    clasMap
  }

  private[scalabio] def storingDB(register: Register): Future[Unit] = {
    val registerRepo = RegisterRepository.getInstance();
    val repo =  registerRepo.insertRegister(register)
    Future(repo)
  }

  private[scalabio] def saveMetrics(mapPredictedMetrics: Map[RefactorRegister,RefMetric] ): Future[Unit] = {
    val storingRegisters = mapPredictedMetrics flatMap  { operRef =>
      val res = operRef._2 collect {
        case ref if ref._1.contains( operRef._1.operRef.getRefType.getAcronym ) =>
          ref._2 flatMap { clas => clas._2 map { metric =>
            val register = new Register(
                        operRef._1.operRef.getRefType.getAcronym, metric._1, metric._2,
                        operRef._1.src.getOrElse(""), operRef._1.tgt.getOrElse(""),
                        operRef._1.fld.getOrElse("-1"), operRef._1.mtd.getOrElse("-1"), clas._1
                      )
            storingDB(register)
      }} } flatten

      res
    } toList

    Future.traverse(storingRegisters) { storeR =>
      storeR
    } map (_ => Unit)
  }


}

trait FitnessCache extends FitnessCacheUtils {


  protected[scalabio] def recordarOperacionRefactor(operRef: RefactoringOperation): Future[RefMetric] = {
    val acronym = RefAcronym.withName( operRef.getRefType.getAcronym )

    lazy val rMetrics = ( if(operRef.getParams != null ){
      //1. If is params defined
      val refactParam = if(acronym == RefAcronym.EC){
        retrieveMetrics(extractParamsEC(operRef), acronym)
      } else {
        retrieveMetrics(extractParams(operRef), acronym)
      }

      val res:RefMetric = if (refactParam.isEmpty) Map.empty else Map(acronym.toString -> refactParam)
      res
    } else{
      //2. If is not params defined
      Map.empty
    }).toMap[String,Map[String, Map[String, Double]]]

    Future(rMetrics)
  }

  protected[scalabio] def recallRefactoringRecommendation(listRef: Set[RefactoringOperation]): Future[RefMetric] ={
    //Fixme bad implemented with flatten, needs better merge
    val res = Future.traverse(listRef){ x =>
      recordarOperacionRefactor(x)
    } map(_.flatten.toMap)
    res
  }

  protected[scalabio] def memorize(listRef: List[RefactoringOperation]): Future[RefMetric] = {
    //La memorización debería llamar directamente la predicción, si recuerdo no tengo porqué predecir

    val predictedMetricsMap = listRef map{ operRef =>
      val predictedMetric:RefMetric = {
        val listOperRef = MetricCalculator.predictMetrics( List(operRef) ,
          MetaphorCode.getMetrics, MetaphorCode.getPrevMetrics )

        val rr = (mapAsScalaMap(listOperRef) map {x =>
          x._1 -> (mapAsScalaMap(x._2) map {
            y => y._1 -> mapAsScalaMap(y._2).toMap}
            ).toMap }).toMap

        rr.asInstanceOf[RefMetric]
      }

      val refactParam =  if(operRef.getRefType.getAcronym == RefAcronym.EC.toString){
          extractParamsEC(operRef)
        } else {
          extractParams(operRef)
        }

      refactParam -> predictedMetric
    } toMap

    val res = saveMetrics(predictedMetricsMap) map { _ =>
      (predictedMetricsMap.values flatten ).toMap
    }
    res
  }

  protected[scalabio] def memorizeSingle(refOper: RefactoringOperation): Future[RefMetric] = {
    //La memorización debería llamar directamente la predicción,
    //si recuerdo no tengo porqué predecir

    val predictedMetricsMap = {
      val predictedMetric:RefMetric = {
        val listOperRef = MetricCalculator.predictMetrics( List(refOper) ,
          MetaphorCode.getMetrics, MetaphorCode.getPrevMetrics )

        val rr = (mapAsScalaMap(listOperRef) map {x =>
          x._1 -> (mapAsScalaMap(x._2) map {
            y => y._1 -> mapAsScalaMap(y._2).toMap}
            ).toMap }).toMap

        rr.asInstanceOf[RefMetric]
      }

      val refactParam =  if(refOper.getRefType.getAcronym == RefAcronym.EC.toString){
        extractParamsEC(refOper)
      } else {
        extractParams(refOper)
      }

      Map(refactParam -> predictedMetric)
    }

    val res = saveMetrics(predictedMetricsMap) map { _ =>
      (predictedMetricsMap.values flatten ).toMap
    }
    res
  }
  
}

trait FitnessBias extends FitnessCache{

  private def predictMetrics(refOperations: Set[RefactoringOperation]): Future[List[RefMetric]] = {
    //Fixme Here we can trace time of caché
    //Sino se puede recordar la operación entonces la memoriza (predecir + almacenar)
    //Con un set se asegura que no puede predecir operaciones de refacotoring repetidas
    val listRefMetric = refOperations map{ refOper =>
      (for{
        recalledRefOper <- recordarOperacionRefactor(refOper)
      } yield recalledRefOper) flatMap  {
        case rrefOper if rrefOper.nonEmpty =>
          Future(rrefOper)
        case rrefOper if rrefOper.isEmpty =>
          memorizeSingle(refOper)
      }
    }

    val res = Future.traverse(listRefMetric.toList){ x => x}
    res
  }

  private def simplifyingMetric( classListMap : List[Metric], result: Metric): Metric = {
    classListMap match {
      case Nil ⇒
        result
      case head :: tail ⇒
        val filterHead = head collect {
          case (key,value) if result.get(key).fold(true) { _ < value } ⇒
            (key,value)
        }
        simplifyingMetric( tail , result ++ filterHead )
    }
  }

  private def simplifyingClass( classListMap : List[ClassMap], result: ClassMap): ClassMap = {
    classListMap match {
      case Nil ⇒
        result
      case head :: tail ⇒
        val filterHead = head collect {
          case(key, value) if result.contains(key) ⇒
            val arValue = simplifyingMetric( List(value), result.getOrElse(key,Map.empty) )
            (key,arValue)
          case(key, value) if !result.contains(key) ⇒
            (key,value)
        }
        simplifyingClass( tail, result ++ filterHead)
    }
  }

  private def getSUA(refactoringsListMap : List[RefMetric]): ClassMap = {
    //Organized the prediction and reduce the data according to maximum value for metrics
    //SUA is composed of classes (witout refactorings)

    def simplifyingRefactoring( refactoringsListMap : List[RefMetric], result: RefMetric): RefMetric = {
      refactoringsListMap match {
        case Nil ⇒
          result
        case head :: tail ⇒
          val filterHead = head collect {
            case (key,value) if result.contains(key) ⇒
              val arValue = simplifyingClass(List(value), result.getOrElse(key,Map.empty))
              (key,arValue)
            case (key,value) if !result.contains(key) ⇒
              (key, value)
          }
          simplifyingRefactoring( tail, result ++ filterHead)
      }
    }

    val sua2 = refactoringsListMap reduce(_ ++ _)

    val sua = simplifyingRefactoring(refactoringsListMap, Map.empty).values.toSet.flatten.toMap

    sua
  }

  private def reduceMetricsBySum( map1 : Metric, map2: Metric): Metric = {
    val mm = map1 collect {
      case (key, value) if map2.contains(key) ⇒
        val retrievedValue: Double = map2.getOrElse(key, 0.0)
        (key, retrievedValue + value)
      case (key, value) if !map2.contains(key) ⇒
        (key, value)
    }
    map2 ++ mm
  }

  protected[scalabio] def biasQualitySystemRatio(refOperations: Set[RefactoringOperation]): Double ={
    val generalQuality = predictMetrics(refOperations) map { refF =>

      val metricActualVector = getSUA(refF)
      val suaMetric = metricActualVector.values.toList reduceLeft( (x,y) => {reduceMetricsBySum(x,y)} )
      val suaPrevMetric = (metricActualVector map { oneClass =>
        val prevMetric = (MetaphorCode.getPrevMetrics.toMap map { met =>
          (met._1, met._2.toMap map{ m => (m._1,m._2.toDouble)} ) }).getOrElse(oneClass._1,Map.empty)
        (oneClass._1, prevMetric)
      }).values.toList reduceLeft((x,y) => {reduceMetricsBySum(x,y)})

      if(suaPrevMetric.isEmpty){
        suaPrevMetric
      }

      val min = suaMetric.values.toList.reduceLeft( (x,y) => {if(x<y) x else y} )
      val max = suaMetric.values.toList.reduceLeft( (x,y) => {if(x>y) x else y} )

      val minPrev = suaPrevMetric.values.toList.reduceLeft( (x,y) => {if(x<y) x else y} )
      val maxPrev = suaPrevMetric.values.toList.reduceLeft( (x,y) => {if(x>y) x else y} )

      //Vector weights for metrics
      val w = 1.0 / suaMetric.size

      //Normalization
      val numerator = (suaMetric map {met => { w * ((met._2 - min)/(max - min)) } }).toList.reduceLeftOption(_ + _)
      val denominator = (suaPrevMetric map {met => { w * ((met._2 - minPrev)/(maxPrev - minPrev)) } }).toList.reduceLeftOption(_ + _)
      (numerator flatMap ( x => denominator map( y => x/y) )).getOrElse(1.0)
    }
    //generalQuality.
    Await.result(generalQuality, 1000000 millis)
  }

}

class FitnessScalaApply extends FitnessBias {
  def gBiasQualitySystemRatio(refOperations: java.util.List[RefactoringOperation]) : Double ={
    biasQualitySystemRatio(refOperations.toSet)
  }
}
