package space;

import entity.Qubit;
import entity.QubitArray;
import unalcol.search.space.Space;

public class QubitSpace extends Space<QubitArray> {
	protected int n;
	protected int QubitTam;
	
	public QubitSpace( int n, int QubitTam ){
		this.QubitTam = QubitTam;
		this.n = n; 
	}

	@Override
	public boolean feasible(QubitArray x) {
		return x.size()==n;
	}

	@Override
	public double feasibility(QubitArray x) {
		return feasible(x)?1:0;
	}

	@Override
	public QubitArray repair(QubitArray x) {
		if( x.size() != n ){
			if(x.size()>n){
				x = x.subQubitArray(0,n);
			}else{
				//x = new QubitArray(n, true);
				for( int i=0; i<n;i++)
					x.set(new Qubit(true, QubitTam)); //el entero ingresado debe estar como constructor del espacio
			}
		}
		return x;
	}

	@Override
	public QubitArray get() {
		return new QubitArray(n, true, QubitTam); //el entero ingresado debe estar como constructor del espacio
	}
}
