import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ServerUI extends JFrame {
	
	/**
	 * TODOS
	 * check for user input- only letters/ specific keywords
	 * restore to default button
	 * switch to layout 2 button
	 * better image for phone, arrows?
	 */
	
	/**
	 * Use values from save button's actionlistener
	 */

	private static final int PORT = 8888;
	private static final int FPS_MIN = 0;
	private static final int FPS_MAX = 30;
	private static final int FPS_INIT = 15; // initial frames per second
	private static final String MESSAGETOUSER = "Please go to the settings tab to map PC controls to android touch controls.";
	private String directionKeys[] = { "^ v < >", "W A S D" };

	private Object[][] defaultData = { { "Tap", "Mouse Left Button" },
			{ "Swipe Up", "X" }, { "Swipe Left", "B" }, { "Swipe Down", "C" },
			{ "Swipe Right", "V" } };

	private Object[][] emptyData = { { "Tap", "None" }, { "Swipe Up", "None" },
			{ "Swipe Left", "None" }, { "Swipe Down", "None" },
			{ "Swipe Right", "None" } };

	private JTabbedPane tabbedPane;
	private JPanel generalPane;
	private JPanel keySettingsPane;
	private JPanel testKeysPane;
	private JPanel noTiltPane;
	private JPanel tiltUpPane;
	private JPanel tiltDownPane;
	private JPanel tiltRightPane;
	private JPanel tiltLeftPane;
	private JTable table1 = new JTable();
	private JTable table2= new JTable();
	private JTable table3= new JTable();
	private JTable table4= new JTable();
	private JTable table5= new JTable();
	private JTable[] tables = { table1, table2, table3, table3, table4, table5 };

	private HashMap<String, HashMap<String, String>> keyControls = new HashMap<String, HashMap<String, String>>(); // Tilt->
																													// (TouchControl->PCControl)
	private static JLabel status;

	public void createServerUI() throws UnknownHostException {

		ServerUI sui = new ServerUI();
		status = new JLabel(" Connection Status: Disconnected");
		sui.createBasicUI();
		sui.setVisible(true);

	}

	private void createBasicUI() throws UnknownHostException {
		// TODO Auto-generated method stub

		setTitle("Game Controller Server");
		setSize(750, 450);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);

		createGeneralTab();
		createSettingsTab();
		createTestKeysTab();

		// We create a panel which will hold the UI components
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("General", generalPane);
		tabbedPane.addTab("Key Settings", keySettingsPane);
		tabbedPane.addTab("Test Keys", testKeysPane);
		topPanel.add(tabbedPane, BorderLayout.CENTER);

	}

	private void createSettingsTab() {
		// TODO Auto-generated method stub
		/*
		 * keySettingsPane = new ImagePanel(new
		 * File("Images/androidphone.jpg")); keySettingsPane.setLayout(new
		 * GridLayout(8, 1)); keySettingsPane.setBorder(new EmptyBorder(10, 10,
		 * 10, 10));
		 */

		keySettingsPane = new JPanel(new BorderLayout());
		keySettingsPane.setLayout(new FlowLayout(FlowLayout.LEADING));
		keySettingsPane.setBorder(new EmptyBorder(2, 2, 2, 2));

		JLabel dirControl = new JLabel("Direction Keys");
		keySettingsPane.add(dirControl);

		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		for (String value : directionKeys) {
			model.addElement(value);
		}
		final JComboBox<String> comboBox = new JComboBox<String>(model);
		comboBox.setSize(1, 1);
		keySettingsPane.add(comboBox);

		Image img = new ImageIcon("src/Images/androidphone.jpg").getImage();
		Image newimg = img.getScaledInstance(300, 150,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newimg);

		JLabel label = new JLabel(newIcon);

		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		keySettingsPane.add(label); // default center section

		JLabel mouseMove = new JLabel("Mouse Control");
		keySettingsPane.add(mouseMove);

		DefaultComboBoxModel<String> mouseModel = new DefaultComboBoxModel<String>();
		mouseModel.addElement("Hold and Drag");

		final JComboBox<String> comboBoxMouse = new JComboBox<String>(mouseModel);
		comboBoxMouse.setSize(1, 1);
		keySettingsPane.add(comboBoxMouse);

		buildInnerTabs();
		
		JButton switchToLayout2 = new JButton("Switch to Layout 2");
		switchToLayout2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		keySettingsPane.add(switchToLayout2);

		JButton restoreToDefault = new JButton("Restore to default settings");
		restoreToDefault.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		keySettingsPane.add(restoreToDefault);

		JButton save = new JButton("Save changes");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// ... called when button clicked
				String directionKeys = comboBox.getSelectedItem().toString();
				String mouseControl = comboBoxMouse.getSelectedItem().toString();
				storeMappings(tables,0, "No Tilt");
				storeMappings(tables,1, "Tilt Up");
				storeMappings(tables,2, "Tilt Down");
				storeMappings(tables,3, "Tilt Right");
				storeMappings(tables,4, "Tilt Left");
				for (Map.Entry entry : keyControls.entrySet()) {
				    System.out.println("key controls "+entry.getKey() + ", " + entry.getValue());
				}
				JOptionPane.showMessageDialog(null, "Direction keys: "
						+ directionKeys + "  Mouse control: " + mouseControl
						+ "\nAll changes saved successfully." );
					}
						});
		keySettingsPane.add(save);

	}

	public void storeMappings(JTable[] tables,int index, String tabName) {
		System.out.println("CLICKED");

		for (int i = 0; i < tables[index].getRowCount(); i++) {
			String touchControl = tables[index].getValueAt(i, 0).toString();
			String pcControl = tables[index].getValueAt(i, 1).toString();
			if (!pcControl.equals("None")) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put(tabName, touchControl);
				keyControls.put(pcControl, hm);
				System.out.println("PRINT"+tabName+" "+pcControl+" "+touchControl+" "+keyControls.size());
			}
		}
	}

	private void buildInnerTabs() {
		// TODO Auto-generated method stub
		JTabbedPane innerTabbedPane = new JTabbedPane();
		innerTabbedPane.setPreferredSize(new Dimension(450, 150));
		setDefaultSettings();

		innerTabbedPane.addTab("No Tilt", noTiltPane);
		innerTabbedPane.addTab("Tilt up", tiltUpPane);
		innerTabbedPane.addTab("Tilt down", tiltDownPane);
		innerTabbedPane.addTab("Tilt right", tiltRightPane);
		innerTabbedPane.addTab("Tilt left", tiltLeftPane);

		keySettingsPane.add(innerTabbedPane, BorderLayout.CENTER);
	}

	public void setDefaultSettings() {
		noTiltPane = createTiltTab(defaultData, tables,0);
		tiltUpPane = createTiltTab(emptyData, tables,1);
		tiltDownPane = createTiltTab(emptyData, tables,2);
		tiltRightPane = createTiltTab(emptyData, tables,3);
		tiltLeftPane = createTiltTab(emptyData, tables,4);
	}

	private JPanel createTiltTab(Object[][] data, JTable[] tables, int index) {
		// TODO Auto-generated method stub
		JPanel pane = new JPanel(new BorderLayout());
		// noTiltPane.setLayout(new GridBagLayout());
		// noTiltPane.setBorder(new EmptyBorder(2,2,2,2));

		String[] columnNames = { "Touch Controls", "PC Controls" };

		tables[index] = new JTable(data, columnNames) {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return getPreferredSize().width < getParent().getWidth();
			}

			@Override
			// left column is not editable
			public boolean isCellEditable(int row, int column) {
				if (column == 0) {
					return false;
				} else {
					return true;
				}
			}
		};
		JScrollPane jsp = new JScrollPane(tables[index],
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tables[index].setRowHeight(20);
		tables[index].setPreferredScrollableViewportSize(tables[index].getPreferredSize());
		pane.add(jsp);

		return pane;
	}

	private void createTestKeysTab() {
		// TODO Auto-generated method stub
		testKeysPane = new JPanel(new BorderLayout());
		testKeysPane.setLayout(new GridLayout(8, 1));
		testKeysPane.setBorder(new EmptyBorder(10, 10, 10, 10));
	}

	private void createGeneralTab() throws UnknownHostException {
		// TODO Auto-generated method stub
		generalPane = new JPanel(new BorderLayout());
		generalPane.setLayout(new GridLayout(8, 1));
		generalPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel heading = new JLabel(" Server Configuration ");
		heading.setFont(new Font(heading.getName(), Font.BOLD, 20));
		generalPane.add(heading);

		JLabel ipConfig = new JLabel(" Host Name/ IP Address: "
				+ InetAddress.getLocalHost());
		generalPane.add(ipConfig);

		JLabel port = new JLabel(" Port: " + PORT);
		generalPane.add(port);

		// change this dynamically
		generalPane.add(status);

		generalPane.add(new JSeparator(SwingConstants.HORIZONTAL));

		JLabel mouseCursor = new JLabel(" Mouse Cursor Sensitivity ");
		mouseCursor.setFont(new Font(mouseCursor.getName(), Font.BOLD, 20));
		generalPane.add(mouseCursor);

		JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, FPS_MIN,
				FPS_MAX, FPS_INIT);
		framesPerSecond.addChangeListener(new SliderListener());

		framesPerSecond.setMajorTickSpacing(10);
		framesPerSecond.setMinorTickSpacing(1);
		framesPerSecond.setPaintTicks(true);
		framesPerSecond.setPaintLabels(true);

		generalPane.add(framesPerSecond);

		JLabel msgToUser = new JLabel(MESSAGETOUSER);
		msgToUser.setFont(new Font("Serif", Font.ITALIC, 15));
		generalPane.add(msgToUser);

	}

	public void updateStatus(boolean value) {
		// TODO
		// call this function whenever a connection is made or broken
		if (value) {
			status.setText(" Connection Status: Connected");
			// System.out.println("CONNECTED");
		} else {
			status.setText(" Connection Status: Disconnected");
			// System.out.println("DISCONNECTED");
		}
	}

}

class SliderListener implements ChangeListener {

	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			int fps = (int) source.getValue();
			System.out.println("Slider value" + fps);
			// TODO
			// change according to mouse cursor movement
		}
	}
}