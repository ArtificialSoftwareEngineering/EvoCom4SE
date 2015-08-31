package optimization;

import entity.Qubit;
import entity.QubitArray;
import unalcol.search.space.Space;

public class QubitSpace extends Space<QubitArray> {
	protected int n;
	
	public QubitSpace( int n ){
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
					x.set(new Qubit(true));
			}
		}
		return x;
	}

	@Override
	public QubitArray get() {
		return new QubitArray(n, true);
	}
}
