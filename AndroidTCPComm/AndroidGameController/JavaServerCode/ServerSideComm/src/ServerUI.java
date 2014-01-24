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

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
	 * TODOS check for user input- only letters specific keywords, better image
	 * for phone, arrows?
	 */

	/**
	 * Use values from save button's actionlistener
	 */

	private JTabbedPane innerTabbedPane;
	private JPanel tableHolder;
	private int connectionType = 1;
	private static final int PORT = 8888;
	private static final int FPS_MIN = 0;
	private static final int FPS_MAX = 30;
	private JComboBox<String> comboBox;
	private JComboBox<String> comboBoxMouse;
	private JTable keyTable;
	private static int userChoice=0;
	private static final int FPS_INIT = 15; // initial frames per second
	private static final String MESSAGETOUSER = "Please go to the settings tab to map PC controls to android touch controls.";
	private String directionKeys[] = { "^ v < >", "W A S D" };

	private Object[][] defaultData = { { "Tap", "LMB" }, { "Swipe Up", "X" },
			{ "Swipe Left", "B" }, { "Swipe Down", "C" },
			{ "Swipe Right", "V" } };

	private Object[][] emptyData = { { "Tap", "None" }, { "Swipe Up", "None" },
			{ "Swipe Left", "None" }, { "Swipe Down", "None" },
			{ "Swipe Right", "None" } };

	private Object[][] layout2defaultData = { { "Tap", "LMB" },
			{ "Swipe Up", "X" }, { "Swipe Down", "C" }, { "Swipe Left", "V" },
			{ "Swipe Right", "B" } };

	private String[] columnNames = { "Touch Controls", "PC Controls" };

	private JTabbedPane tabbedPane;
	private JTabbedPane keySettingsTabbedPane;
	private JPanel generalPane;
	private JPanel keySettingsPane;
	private JPanel testKeysPane;
	private JPanel layout1;
	private JPanel layout2;
	private JPanel noTiltPane;
	private JPanel tiltUpPane;
	private JPanel tiltDownPane;
	private JPanel tiltRightPane;
	private JPanel tiltLeftPane;
	private JTable table1 = new JTable();
	private JTable table2 = new JTable();
	private JTable table3 = new JTable();
	private JTable table4 = new JTable();
	private JTable table5 = new JTable();
	private JTable[] tables = { table1, table2, table3, table4, table5 };

	private HashMap<String, HashMap<String, String>> keyControls = new HashMap<String, HashMap<String, String>>(); // TouchControl->(tilt->PCControl)
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
		setSize(750, 500);

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
		//tabbedPane.addTab("Test Keys", testKeysPane);
		topPanel.add(tabbedPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void resetOriginalValues() {

		Object[][] originalDefaultData = { { "Tap", "LMB" },
				{ "Swipe Up", "X" }, { "Swipe Left", "B" },
				{ "Swipe Down", "C" }, { "Swipe Right", "V" } };

		Object[][] originalEmptyData = { { "Tap", "None" },
				{ "Swipe Up", "None" }, { "Swipe Left", "None" },
				{ "Swipe Down", "None" }, { "Swipe Right", "None" } };
		defaultData = originalDefaultData;
		emptyData = originalEmptyData;
	}

	private void createSettingsTab() {
		// TODO Auto-generated method stub

		keySettingsPane = new JPanel();
		keySettingsPane.setLayout(new BorderLayout());
		createLayout1();
		createLayout2();

		keySettingsTabbedPane = new JTabbedPane();
		keySettingsTabbedPane.addTab("Layout 1", layout1);
		keySettingsTabbedPane.addTab("Layout 2", layout2);
		keySettingsPane.add(keySettingsTabbedPane, BorderLayout.CENTER);

	}

	private void createLayout2() {
		// TODO Auto-generated method stub
		layout2 = new JPanel(new BorderLayout());
		layout2.setLayout(new FlowLayout(FlowLayout.LEADING));
		layout2.setBorder(new EmptyBorder(2, 2, 2, 2));

		buildComponentsInKeySettings(layout2, "Phone Tilt");

		buildLayout2Table(layout2defaultData);

		JButton switchToLayout2 = new JButton("Switch to Layout 1");
		switchToLayout2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				keySettingsTabbedPane.setSelectedIndex(0);
			}
		});
		layout2.add(switchToLayout2);

		JButton restoreToDefault = new JButton("Restore to default settings");
		restoreToDefault.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				// TODO TABLE

				// direction keys
				comboBox.setSelectedIndex(0);
				Object[][] originalData = { { "Tap", "LMB" },
						{ "Swipe Up", "X" }, { "Swipe Down", "C" },
						{ "Swipe Left", "V" }, { "Swipe Right", "B" } };
				layout2defaultData = originalData;
				tableHolder.removeAll();

				keyTable = new JTable(layout2defaultData, columnNames) {
					@Override
					// left column is not editable
					public boolean isCellEditable(int row, int column) {
						if (column == 0) {
							//keyTable.setCellSelectionEnabled(false);
							return false;
						} else {
							return true;
						}
					}
				};
				
				keyTable.setFocusable(false);

				JScrollPane jspane = new JScrollPane(keyTable,
						ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
						ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				keyTable.setRowHeight(20);
				keyTable.setPreferredScrollableViewportSize(new Dimension(450,
						100));
				tableHolder.add(jspane);
				keySettingsTabbedPane.setSelectedIndex(0);
				keySettingsTabbedPane.setSelectedIndex(1);
			}
		});
		
		layout2.add(restoreToDefault);
		
		JButton save = new JButton("Save changes");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// ... called when button clicked
				String directionKeys = comboBox.getSelectedItem().toString();
				String mouseControl = comboBoxMouse.getSelectedItem()
						.toString();

				boolean flag = false;
				for (int i = 0; i < layout2defaultData.length; i++) {
					// use these values (only unique ones) 
					System.out.println("Command: " + layout2defaultData[i][0]
							+ " Key " + layout2defaultData[i][1]);
					if (!validateKey(layout2defaultData[i][1])) {
						JOptionPane
								.showMessageDialog(
										null,
										"Only A-Z, 0-9, LMB, RMB, Ctrl, Space, Alt and Shift keys are allowed.\nPlease use 'None' to delete a button mapping");
						flag = true;
						break;
					}
				}
				if (flag == false) // save the mappings // if flag is true then
									// dont save
					JOptionPane.showMessageDialog(null, "Direction keys: "
							+ directionKeys + "  Mouse control: "
							+ mouseControl
							+ "\nAll changes saved successfully.");
			}

		});
		layout2.add(save);

		JLabel abbreviations = new JLabel(
				"** LMB= Left Mouse Button RMB= Right Mouse Button");
		layout2.add(abbreviations);
		JLabel warning = new JLabel("Warning: In case of duplicate entries, only the FIRST one will be mapped.");
		layout2.add(warning);

	}

	private boolean validateKey(Object object) {
		// TODO Auto-generated method stub
		String input = object.toString().trim();
		System.out.println("keu" + input);
		if (input.length() > 1) {
			if (input.equalsIgnoreCase("CTRL")
					|| input.equalsIgnoreCase("SHIFT")
					|| input.equalsIgnoreCase("SPACE")
					|| input.equalsIgnoreCase("ALT")
					|| input.equalsIgnoreCase("LMB")
					|| input.equalsIgnoreCase("RMB")
					|| input.equalsIgnoreCase("None"))
				return true;
			else
				return false;
		} else {
			char character = input.charAt(0);
			if (Character.isDigit(character) || Character.isLetter(character))
				return true;
			else
				return false;
		}
	}

	public void buildLayout2Table(Object[][] data) {

		tableHolder = new JPanel();
		keyTable = new JTable(data, columnNames) {
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
		JScrollPane jspane = new JScrollPane(keyTable,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		keyTable.setRowHeight(20);
		keyTable.setPreferredScrollableViewportSize(new Dimension(450, 100));
		tableHolder.add(jspane);
		layout2.add(tableHolder);
	}

	public void buildComponentsInKeySettings(JPanel layout, String mouseElement) {
		JLabel dirControl = new JLabel("Direction Keys");
		layout.add(dirControl);
		DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
		for (String value : directionKeys) {
			model.addElement(value);
		}
		comboBox = new JComboBox<String>(model);
		comboBox.setSize(1, 1);
		layout.add(comboBox);

		Image img = new ImageIcon("src/Images/androidphone.jpg").getImage();
		Image newimg = img.getScaledInstance(300, 150,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon newIcon = new ImageIcon(newimg);

		JLabel label = new JLabel(newIcon);

		label.setHorizontalAlignment(JLabel.CENTER);
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		layout.add(label); // default center section

		JLabel mouseMove = new JLabel("Mouse Control");
		layout.add(mouseMove);

		DefaultComboBoxModel<String> mouseModel = new DefaultComboBoxModel<String>();
		mouseModel.addElement(mouseElement);

		comboBoxMouse = new JComboBox<String>(mouseModel);
		comboBoxMouse.setSize(1, 1);
		layout.add(comboBoxMouse);
	}

	private void createLayout1() {
		// TODO Auto-generated method stub
		layout1 = new JPanel(new BorderLayout());
		layout1.setLayout(new FlowLayout(FlowLayout.LEADING));
		layout1.setBorder(new EmptyBorder(2, 2, 2, 2));

		buildComponentsInKeySettings(layout1, "Hold and Drag");

		buildInnerTabs();

		JButton switchToLayout2 = new JButton("Switch to Layout 2");
		switchToLayout2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				keySettingsTabbedPane.setSelectedIndex(1);
			}
		});
		layout1.add(switchToLayout2);

		JButton restoreToDefault = new JButton("Restore to default settings");
		restoreToDefault.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				innerTabbedPane.removeAll();
				resetOriginalValues();
				setDefaultSettings();
				populateTabs(innerTabbedPane);

				// direction keys
				comboBox.setSelectedIndex(0);

			}
		});

		layout1.add(restoreToDefault);

		JButton save = new JButton("Save changes");
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// ... called when button clicked
				String directionKeys = comboBox.getSelectedItem().toString();
				String mouseControl = comboBoxMouse.getSelectedItem()
						.toString();
				keyControls = new HashMap<String, HashMap<String, String>>();
				storeMappings(tables, 0, "No Tilt");
				storeMappings(tables, 1, "Tilt Up");
				storeMappings(tables, 2, "Tilt Down");
				storeMappings(tables, 3, "Tilt Right");
				storeMappings(tables, 4, "Tilt Left");
				boolean flag = false;
				for (Map.Entry entry : keyControls.entrySet()) {

					if (!validateKey(entry.getKey())) {
						JOptionPane.showMessageDialog(
										null,
										"Only A-Z, 0-9, LMB, RMB, Ctrl, Space, Alt and Shift keys are allowed.\nPlease use 'None' to delete a button mapping");
						flag = true;
						System.out.println("key controls " + entry.getKey()
								+ ", " + entry.getValue());
						break;
					}
				}

				if (flag == false)
					JOptionPane.showMessageDialog(null, "Direction keys: "
							+ directionKeys + "  Mouse control: "
							+ mouseControl
							+ "\nAll changes saved successfully.");
			}
		});
		layout1.add(save);

		JLabel abbreviations = new JLabel(
				"** LMB= Left Mouse Button   RMB= Right Mouse Button");
		layout1.add(abbreviations);
		JLabel warning = new JLabel("Warning: In case of duplicate entries, only the FIRST one will be mapped.");
		layout1.add(warning);

	}

	public void storeMappings(JTable[] tables, int index, String tabName) {

		for (int i = 0; i < tables[index].getRowCount(); i++) {
			String touchControl = tables[index].getValueAt(i, 0).toString();
			String pcControl = tables[index].getValueAt(i, 1).toString();
			if (!pcControl.equals("None")) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put(tabName, touchControl);
				keyControls.put(pcControl, hm);
				System.out.println("PRINT" + tabName + " " + pcControl + " "
						+ touchControl + " " + keyControls.size());
			}
		}
	}

	private void buildInnerTabs() {
		// TODO Auto-generated method stub
		innerTabbedPane = new JTabbedPane();
		innerTabbedPane.setPreferredSize(new Dimension(450, 150));
		setDefaultSettings();

		populateTabs(innerTabbedPane);
		layout1.add(innerTabbedPane, BorderLayout.CENTER);
	}

	public void populateTabs(JTabbedPane innerTabbedPane) {
		innerTabbedPane.addTab("No Tilt", noTiltPane);
		innerTabbedPane.addTab("Tilt up", tiltUpPane);
		innerTabbedPane.addTab("Tilt down", tiltDownPane);
		innerTabbedPane.addTab("Tilt right", tiltRightPane);
		innerTabbedPane.addTab("Tilt left", tiltLeftPane);
	}

	public void setDefaultSettings() {
		noTiltPane = createTiltTab(defaultData, tables, 0);
		tiltUpPane = createTiltTab(emptyData, tables, 1);
		tiltDownPane = createTiltTab(emptyData, tables, 2);
		tiltRightPane = createTiltTab(emptyData, tables, 3);
		tiltLeftPane = createTiltTab(emptyData, tables, 4);
	}

	private JPanel createTiltTab(Object[][] data, JTable[] tables, int index) {
		// TODO Auto-generated method stub
		JPanel pane = new JPanel(new BorderLayout());

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
		tables[index].setPreferredScrollableViewportSize(tables[index]
				.getPreferredSize());
		pane.add(jsp);

		resetOriginalValues();
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
		generalPane.setLayout(new GridLayout(10,1));
		generalPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		JLabel heading = new JLabel(" Server Configuration ");
		heading.setFont(new Font(heading.getName(), Font.BOLD, 20));
		generalPane.add(heading);
		
		JLabel connectionMode= new JLabel(" Choose your preferred connection type: ");
		generalPane.add(connectionMode);
		
		JRadioButton wifiButton = new JRadioButton("Wifi");
	    wifiButton.setSelected(true);
	    
		JRadioButton bluetoothButton = new JRadioButton("Bluetooth");
		final JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int reply= JOptionPane.showConfirmDialog(null,"Once confirmed, you will be not be able to change your choice!","Connect now?",JOptionPane.YES_NO_OPTION);
				
				if(reply==JOptionPane.YES_OPTION){
					connect.setText("Choice confirmed");
					userChoice= getConnectionType();
					connect.setEnabled(false);
				}
			}
		});
		
		ButtonGroup group = new ButtonGroup();
        group.add(wifiButton);
        group.add(bluetoothButton);
        group.add(connect);

        wifiButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectionType=1;
				System.out.println(connectionType+"connectionType");
			}
		});
              
        bluetoothButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectionType=2;
				System.out.println(connectionType+"connectionType");
			}
		});
        
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.LINE_AXIS));
        radioPanel.add(wifiButton);
        radioPanel.add(bluetoothButton);
        radioPanel.add(connect);
        radioPanel.setVisible(true);
        generalPane.add(radioPanel);

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
	
	public int getConnectionType() {
		return connectionType;
	}
 
	public void updateStatus(boolean value) {
		// TODO
		// call this function whenever a connection is made or broken
		if (value) {
			status.setText(" Connection Status: Connected");
		} else {
			status.setText(" Connection Status: Disconnected");
		}
	}

	public int getUserChoice() {
		return userChoice;
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