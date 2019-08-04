package appFX.framework.utils;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class Action {
	private boolean inverse = false;
	
	public void apply() {
		if(inverse)
			undoActionFunction();
		else
			applyActionFunction();
	}
	
	public void undo() {
		if(inverse)
			applyActionFunction();
		else
			undoActionFunction();
	}
	
	public void toggleInverse() {
		setInverse(!inverse);
	}
	
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}
	
	public boolean isInversed() {
		return inverse;
	}
	
	protected abstract void applyActionFunction();
	protected abstract void undoActionFunction();
	
	public static abstract class MultipleAction extends Action {
		final LinkedList<Action> actionsInOrder = new LinkedList<>();
		
		public MultipleAction() {
			LinkedList<Action> actions = new LinkedList<>();
			initActionQueue(actions);
			for(Action action : actions)
				actionsInOrder.offerLast(action);
		}
		
		public abstract void initActionQueue(LinkedList<Action> actions);
		
		@Override
		protected void applyActionFunction() {
			for(Action action : actionsInOrder)
				action.applyActionFunction();
		}

		@Override
		protected void undoActionFunction() {
			Iterator<Action> iterator = actionsInOrder.descendingIterator();
			while(iterator.hasNext())
				iterator.next().undoActionFunction();
		}
		
	}
}
