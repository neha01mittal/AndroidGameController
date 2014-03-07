import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
/**
 * This class builds a GUI for server handler that runs on the PC. 
 * The server handler is used for connecting the PC to the android device via Wifi or bluetooth
 * It consists of two tabs- General Settings (for ip address and port display) and Key Settings (for setting key configrations)
 */
public class ServerUI extends JFrame {

	private static final String MOUSE_CURSOR_SENSITIVITY = " Mouse Cursor Sensitivity ";
	private static final String IP_ADDRESS = " IP Address: ";
	private static final String CHOICE_CONFIRMED = "Choice confirmed";
	private static final String CHOOSE_YOUR_PREFERRED_CONNECTION_TYPE = " Choose your preferred connection type: ";
	private static final String SERVER_CONFIGURATION = " Server Configuration ";
	private static final String ERROR_CREATING_NEW_FILE = "Error creating new file: ";
	private static final String CORRUPT_FILE_ERROR = "File corrupted/ missing.. Restoring default values..";
	private static final String SMART_CONTROLLER_DIRECTORY = "SmartController/Data";
	private static final String KEY_MAPPINGS_FILE = "keyMappings.txt";
	private static final String DIRECTION_KEYS = "Direction keys: ";
	private static final String MOUSE_CONTROL = "  Mouse control: ";
	private static final String ALL_CHANGES_SAVED_SUCCESSFULLY = "\nAll changes saved successfully.";
	private static final String TILT_RIGHT = "Tilt Right";
	private static final String TILT_LEFT = "Tilt Left";
	private static final String TILT_DOWN = "Tilt Down";
	private static final String TILT_UP = "Tilt Up";
	private static final String NO_TILT = "No Tilt";
	private static final String SAVE_CHANGES = "Save changes";
	private static final String RESTORE_TO_DEFAULT_SETTINGS = "Restore to default settings";
	private static final String PLEASE_MAP_YOUR_BUTTON_CONFIGURATION_HERE = "Please map your button configuration here.";
	private static final String GAME_CONTROLLER_SERVER = " SmartController Server";
	private JTabbedPane innerTabbedPane;
	private static JLabel status;
	private int connectionType = 1;
	private static final int PORT = 8888;
	private static final int FPS_MIN = 10;
	private static final int FPS_MAX = 50;
	private JComboBox comboBox;
	private JComboBox comboBoxMouse;
	private static int userChoice = 0;
	private static final int FPS_INIT = 15; // initial frames per second
	private static final String MESSAGETOUSER = "Please go to the settings tab to map PC controls to android touch controls.";
	private String directionKeys[] = { "^ v < >", "W A S D" };
	private String defaultKeyboardControls[] = { "-None-", "A", "B", "C", "D",
			"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
			"R", "S", "T", "U", "V", "W", "X", "Y", "Z", "1", "2", "3", "4",
			"5", "6", "7", "8", "9", "0", "SPACEBAR", "CTRL", "ALT", "SHIFT",
			"TAB", "ENTER", "BACKSPACE", "CAPSLOCK", "LEFT-MOUSE-BUTTON",
			"RIGHT-MOUSE-BUTTON" }; // TODO-add more?
	private Object[][] defaultData = { { "Tap", "Z" },
			{ "Swipe Up", "SPACEBAR" }, { "Swipe Down", "D" },
			{ "Swipe Left", "A" }, { "Swipe Right", "X" } };

	private Object[][] emptyData = { { "Tap", "-None-" },
			{ "Swipe Up", "-None-" }, { "Swipe Down", "-None-" },
			{ "Swipe Left", "-None-" }, { "Swipe Right", "-None-" } };

	private String[] columnNames = { "Touch Controls", "PC Controls" };
	private static ArrayList<String> keyboardControlMapping = new ArrayList<String>();

	private static float mouseRatio;

	private JTabbedPane tabbedPane;
	private JPanel generalPane;
	private JPanel keySettingsPane;
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

	public void createServerUI() throws UnknownHostException {
		ServerUI sui = new ServerUI();
		status = new JLabel(" Connection Status: Disconnected");
		sui.createBasicUI();
		sui.setVisible(true);
	}

	private void createBasicUI() throws UnknownHostException {
		// TODO Auto-generated method stub
		setTitle(GAME_CONTROLLER_SERVER);
		setSize(750, 500);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel);
		createGeneralTab();
		createSettingsTab();

		// We create a panel which will hold the UI components
		tabbedPane = new JTabbedPane();
		tabbedPane.addTab("General", generalPane);
		tabbedPane.addTab("Key Settings", keySettingsPane);
		topPanel.add(tabbedPane, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Initialize variables
		mouseRatio = (float) (FPS_INIT * 0.1);
	}
	
	/**
	 * 
	 * @return
	 * userChoice-1- Wifi 2- Bluetooth
	 */
	public int getUserChoice() {
		return userChoice;
	}

	/**
	 * Key settings tab with dropdowns and table for key mappings
	 */
	private void createSettingsTab() {
		keySettingsPane = new JPanel();
		keySettingsPane = new JPanel(new BorderLayout());
		keySettingsPane.setLayout(new FlowLayout(FlowLayout.LEADING));
		keySettingsPane.setBorder(new EmptyBorder(2, 2, 2, 2));

		buildComponentsInKeySettings(keySettingsPane, "Hold and Drag");

		JLabel msgToUser = new JLabel(
				PLEASE_MAP_YOUR_BUTTON_CONFIGURATION_HERE);
		msgToUser.setFont(new Font("Serif", Font.ITALIC, 15));
		keySettingsPane.add(msgToUser);

		buildInnerTabs();

		JButton restoreToDefault = new JButton(RESTORE_TO_DEFAULT_SETTINGS);
		restoreToDefault.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				innerTabbedPane.removeAll();
				resetOriginalValues();
				JPanel[] panels = new JPanel[5];
				for (int i = 0; i < 5; i++) {
					if (i == 0)
						panels[i] = createTiltTab(defaultData, tables, i);
					else
						panels[i] = createTiltTab(emptyData, tables, i);
				}
				assignPanels(panels);
				populateTabs(innerTabbedPane);

				// direction keys
				comboBox.setSelectedIndex(0);
			}
		});

		keySettingsPane.add(restoreToDefault);

		JButton save = new JButton(SAVE_CHANGES);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// ... called when button clicked
				String directionKeys = comboBox.getSelectedItem().toString();
				String mouseControl = comboBoxMouse.getSelectedItem()
						.toString();

				File dataFile = createKeyMapFile();
				PrintWriter writer;
				try {
					writer = new PrintWriter(dataFile, "UTF-8");

					storeMappings(writer, tables, 0, NO_TILT);
					storeMappings(writer, tables, 1, TILT_UP);
					storeMappings(writer, tables, 2, TILT_DOWN);
					storeMappings(writer, tables, 3, TILT_LEFT);
					storeMappings(writer, tables, 4, TILT_RIGHT);
					writer.close();
					JOptionPane.showMessageDialog(null, DIRECTION_KEYS
							+ directionKeys + MOUSE_CONTROL
							+ mouseControl
							+ ALL_CHANGES_SAVED_SUCCESSFULLY);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setDefaultSettings();
				// setKeyMapping();
			}
		});
		keySettingsPane.add(save);
	}

	private void resetOriginalValues() {

		Object[][] originalDefaultData = { { "Tap", "Z" },
				{ "Swipe Up", "SPACEBAR" }, { "Swipe Down", "D" },
				{ "Swipe Left", "A" }, { "Swipe Right", "X" } };

		Object[][] originalEmptyData = { { "Tap", "-None-" },
				{ "Swipe Up", "-None-" }, { "Swipe Down", "-None-" },
				{ "Swipe Left", "-None-" }, { "Swipe Right", "-None-" } };
		defaultData = originalDefaultData;
		emptyData = originalEmptyData;
	}

	/**
	 * store values in a temp file on system
	 */
	public void storeMappings(PrintWriter writer, JTable[] tables, int index,
			String tabName) {
		for (int i = 0; i < tables[index].getRowCount(); i++) {
			String pcControl = tables[index].getValueAt(i, 1).toString();
			writer.print(pcControl + " ");
		}
		writer.println();
	}

	public ArrayList<String> getKeyMappings() {
		return keyboardControlMapping;
	}

	/**
	 * Build tabs (table) for mapping keys to touch 
	 */
	private void buildInnerTabs() {
		// TODO Auto-generated method stub
		innerTabbedPane = new JTabbedPane();
		innerTabbedPane.setPreferredSize(new Dimension(450, 150));
		setDefaultSettings();

		populateTabs(innerTabbedPane);
		keySettingsPane.add(innerTabbedPane, BorderLayout.CENTER);
	}

	/**
	 * count the number of lines in a file
	 * @param file
	 * @return
	 */
	public int getNumberOfLines(File file) {
		int count = 0;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			while ((br.readLine()) != null) {
				count++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * populate table with initial default settings
	 */
	public void setDefaultSettings() {
		// read from file here
		BufferedReader br = null;
		JPanel panels[] = new JPanel[5];
		ArrayList<String> temp = new ArrayList<String>();
		try {
			String sCurrentLine;
			File file = new File(SMART_CONTROLLER_DIRECTORY, KEY_MAPPINGS_FILE);
			int count = 0;
			if (!file.isFile() || getNumberOfLines(file) != 5) {
				// create with default settings
				System.out
						.println(CORRUPT_FILE_ERROR);
				for (int i = 0; i < 5; i++) {
					if (i == 0) {
						panels[i] = createTiltTab(defaultData, tables, i);
						for (int j = 0; j < 5; j++)
							temp.add((String) defaultData[j][1]);
					} else {
						panels[i] = createTiltTab(emptyData, tables, i);
						for (int j = 0; j < 5; j++)
							temp.add((String) emptyData[j][1]);
					}
				}
			} else {
				br = new BufferedReader(new FileReader(file));
				while ((sCurrentLine = br.readLine()) != null) {
					String[] tokens = sCurrentLine.split("\\s+");
					for (int i = 0; i < tokens.length; i++) {
						if (count == 0)
							defaultData[i][1] = tokens[i];
						else
							emptyData[i][1] = tokens[i];
					}
					if (count == 0) {
						panels[0] = createTiltTab(defaultData, tables, 0);
						for (int j = 0; j < 5; j++)
							temp.add((String) defaultData[j][1]);
					} else {
						panels[count] = createTiltTab(emptyData, tables, count);
						for (int j = 0; j < 5; j++)
							temp.add((String) emptyData[j][1]);
					}
					count++;
				}
			}
			keyboardControlMapping.clear();
			for (int i = 0; i < temp.size(); i++) {
				keyboardControlMapping.add(temp.get(i));
			}
			assignPanels(panels);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * create tabs for each table  
	 */
	private JPanel createTiltTab(Object[][] data, final JTable[] tables,
			final int index) {
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
					setColumnSelectionAllowed(false);
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
		TableColumn col = tables[index].getColumnModel().getColumn(1);

		tables[index].setCellSelectionEnabled(true);
		tables[index].getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent lse) {
						if (!lse.getValueIsAdjusting()) {
							// do stuff
							int col = tables[index].getSelectedColumn();
							int row = tables[index].getSelectedRow();
							if (!tables[index].isCellEditable(row, col))
								tables[index].changeSelection(row, col + 1,
										false, false);
						}
					}
				});
		JComboBox<String> comboBox = new JComboBox<String>();
		for (String key : defaultKeyboardControls) {
			comboBox.addItem(key);
		}
		// setKeyMapping();

		col.setCellEditor(new DefaultCellEditor(comboBox));
		pane.add(jsp);

		return pane;
	}

	/**
	 * assign panels 
	 * @param panels
	 */
	public void assignPanels(JPanel[] panels) {
		noTiltPane = panels[0];
		tiltUpPane = panels[1];
		tiltDownPane = panels[2];
		tiltLeftPane = panels[3];
		tiltRightPane = panels[4];
	}

	/**
	 * put each tab's content in the right pane
	 * @param innerTabbedPane
	 */
	public void populateTabs(JTabbedPane innerTabbedPane) {
		innerTabbedPane.addTab(NO_TILT, noTiltPane);
		innerTabbedPane.addTab(TILT_UP, tiltUpPane);
		innerTabbedPane.addTab(TILT_DOWN, tiltDownPane);
		innerTabbedPane.addTab(TILT_LEFT, tiltLeftPane);
		innerTabbedPane.addTab(TILT_RIGHT, tiltRightPane);
	}

	/*
	 * public void setKeyMapping() { File f = new
	 * File("SmartController/Data/keyMappings.txt");
	 * 
	 * if(f.exists()){ //There is an existing keyMap file, read it for keyMap
	 * settings keyboardControlMapping = new
	 * ArrayList<String>(readKeyMapFile()); }else{ //No existing keyMap file,
	 * read from default setting keyboardControlMapping = new
	 * ArrayList<String>(); for(String key: defaultKeyboardControls) {
	 * keyboardControlMapping.add(key); } } }
	 */
	/**
	 * create file for stroing configurations
	 * @return
	 */
	public File createKeyMapFile() {
		File dir = new File(SMART_CONTROLLER_DIRECTORY);
		dir.mkdirs();
		File file = new File(dir, KEY_MAPPINGS_FILE);
		System.out.println("File created: " + file.getAbsolutePath());
		try {
			if (!file.isFile() && !file.createNewFile()) {
				throw new IOException(ERROR_CREATING_NEW_FILE
						+ file.getAbsolutePath());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * add mouse control, gif and direction key dropdown in key settings tab
	 * @param layout
	 * @param mouseElement
	 */
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

		// add the image label
		// TODO get better GIF
		JLabel imageLabel = new JLabel();
		ImageIcon ii = new ImageIcon("src/Images/phoneswipe.gif");
		imageLabel.setIcon(ii);
		layout.add(imageLabel, java.awt.BorderLayout.CENTER);
		// show it
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		JLabel mouseMove = new JLabel("Mouse Control");
		layout.add(mouseMove);

		DefaultComboBoxModel<String> mouseModel = new DefaultComboBoxModel<String>();
		mouseModel.addElement(mouseElement);

		comboBoxMouse = new JComboBox<String>(mouseModel);
		comboBoxMouse.setSize(1, 1);
		layout.add(comboBoxMouse);
	}
	
	/**
	 * add elements to general tab
	 */
	private void createGeneralTab() {
		// TODO Auto-generated method stub
		generalPane = new JPanel(new BorderLayout());
		generalPane.setLayout(new GridLayout(10, 1));
		generalPane.setBorder(new EmptyBorder(10, 10, 10, 10));

		JLabel heading = new JLabel(SERVER_CONFIGURATION);
		heading.setFont(new Font(heading.getName(), Font.BOLD, 20));
		generalPane.add(heading);

		JLabel connectionMode = new JLabel(
				CHOOSE_YOUR_PREFERRED_CONNECTION_TYPE);
		generalPane.add(connectionMode);

		JRadioButton wifiButton = new JRadioButton("Wifi");
		wifiButton.setSelected(true);

		JRadioButton bluetoothButton = new JRadioButton("Bluetooth");
		JRadioButton wifiDirectButton = new JRadioButton("Wifi Direct");
		final JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				int reply = JOptionPane
						.showConfirmDialog(
								null,
								"Once confirmed, you will be not be able to change your choice!",
								"Connect now?", JOptionPane.YES_NO_OPTION);

				if (reply == JOptionPane.YES_OPTION) {
					connect.setText(CHOICE_CONFIRMED);
					userChoice = getConnectionType();
					connect.setEnabled(false);
				}
			}
		});

		ButtonGroup group = new ButtonGroup();
		group.add(wifiButton);
		group.add(bluetoothButton);
		group.add(wifiDirectButton);
		group.add(connect);

		wifiButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectionType = 1;
			}
		});

		bluetoothButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connectionType = 2;
			}
		});

		JPanel radioPanel = new JPanel();
		radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.LINE_AXIS));
		radioPanel.add(wifiButton);
		radioPanel.add(bluetoothButton);
		radioPanel.add(wifiDirectButton);
		radioPanel.add(connect);
		radioPanel.setVisible(true);
		generalPane.add(radioPanel);

		Enumeration<NetworkInterface> e;
		String ip = "";
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while (e.hasMoreElements()) {
				NetworkInterface n = (NetworkInterface) e.nextElement();
				Enumeration<InetAddress> ee = n.getInetAddresses();
				while (ee.hasMoreElements()) {
					InetAddress i = (InetAddress) ee.nextElement();
					if (i.isSiteLocalAddress()) {
						ip += i.getHostName()+": "+i.getHostAddress() + " \n";
					}
				}
			}
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JLabel ipConfig = new JLabel(IP_ADDRESS + ip);
		generalPane.add(ipConfig);

		JLabel port = new JLabel(" Port: " + PORT);
		generalPane.add(port);

		// change this dynamically
		generalPane.add(status);

		generalPane.add(new JSeparator(SwingConstants.HORIZONTAL));

		JLabel mouseCursor = new JLabel(MOUSE_CURSOR_SENSITIVITY);
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

	public float getMouseRatio() {
		return mouseRatio;
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

	/**
	 * control mouse cursor sensitivity 
	 * @author neha01mittal
	 *
	 */
	class SliderListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			if (!source.getValueIsAdjusting()) {
				int fps = (int) source.getValue();
				System.out.println("Slider value" + fps);
				// TODO
				// change according to mouse cursor movement
				mouseRatio = (float) (fps * 0.1);
			}
		}
	}
}