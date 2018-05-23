package main.view;

import java.util.ArrayList;

public class PersonalizedList extends ArrayList<Mask> {

	@Override
	public int indexOf(Object o) {
		int r = -1;
		Mask m = (Mask) o;
		if ( o == null) {
			return -1;
		}
		for (int i =0; i < this.size();i++) {
			Mask mask = (Mask) this.get(i);
			if ( mask.motCouvert.equals(m.motCouvert) && m.start == mask.start) {
				r = i;
				break;
			}
		}
		return r;
	}
	
	
}