import javax.swing.JOptionPane;


public class Test {
	

	public static void main(String args[]) {
		int a =5;
		
		int reply =JOptionPane.showConfirmDialog(null, "Vaishnavi, how are you?");
		
		if(reply==JOptionPane.YES_OPTION) {
			System.out.println("Value of a "+a);
		}
		
		else if(reply==JOptionPane.NO_OPTION)
			System.out.println("NO");
	}

}
