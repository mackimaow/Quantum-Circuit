package testLib;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Test extends JPanel {
	
//	private static final int width = 20;
	
	
	public static void main(String[] args) {
		LinkedList<Integer> integers = new LinkedList<>();
		for(int i = 0; i < 10; i++) {
			integers.add(i);
		}

		printList(integers);
		ListIterator<Integer> iterator = integers.listIterator(0);
		for(int i = 0; i < 5; i++) {
			iterator.add(i);
		}
		printList(integers);
	}
	
	public static void printList(List<?> list) {
		System.out.print("{");
		for(int i = 0; i < list.size(); i++)
			System.out.print(" " + list.get(i));
		System.out.println(" }");
	}
	
	public static void test(Object ... objects) {
		System.out.println(objects.length);
//		for(int i = 0; i < objects.length; i++)
//			System.out.println(objects[i]);
	}
	
	
//	
	
//	private static JFrame initFrame() {
//		JFrame frame = new JFrame();
//		frame.setSize(new Dimension(1000, 600));
//		frame.setLocationRelativeTo(null);
//		return frame;
//	}
//	
//	@Override
//	protected void paintComponent(Graphics g) {
//		Graphics2D g2d = (Graphics2D) g;
//		
//		GateRectangularSelection total = new GateRectangularSelection(5, 5, 10, 10);
//		GateRectangularSelection minus = new GateRectangularSelection(4, 4, 9, 9);
//		GateSelectionBuffer buffer = new GateSelectionBuffer();
//		
//		g2d.setColor(Color.BLACK);
//		drawSelection(g2d, total);
//		drawSelection(g2d, minus);
//		
//		g2d.setColor(Color.RED);
//		buffer.addSelection(total);
//		buffer.intersectSelection(minus);
//		drawSelection(g2d, buffer);
//	}
//	
//	private static void drawSelection(Graphics2D g2d, GateSelectionBuffer selection) {
//		for(GateRectangularSelection rect : selection)
//			drawSelection(g2d, rect);
//	}
//	
//	private static void drawSelection(Graphics2D g2d, GateRectangularSelection selection) {
//		g2d.drawRect(width * selection.getColumnStart(), width * selection.getRowStart(), 
//				width * (selection.getColumnEnd() - selection.getColumnStart()), width * (selection.getRowEnd() - selection.getRowStart()));
//	}
}
