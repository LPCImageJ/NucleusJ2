package gred.nucleus.dialogs;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;


public class AutocropDialog extends JFrame implements ActionListener, ItemListener {
	
	private static final long                 serialVersionUID   = 1L;
	private static final String               INPUT_CHOOSER      = "inputChooser";
	private static final String               OUTPUT_CHOOSER     = "outputChooser";
	private static final String               CONFIG_CHOOSER     = "configChooser";
	
	private final        Container            container;
	private final        JFileChooser         fc                 = new JFileChooser();
	
	private final        JRadioButton          omeroYesButton     = new JRadioButton("Yes");
	private final        JRadioButton          omeroNoButton      = new JRadioButton("No");
	private              boolean               useOmero           = false;
	
	private              JPanel               localModeLayout    = new JPanel();
	private final        JTextField           jInputFileChooser  = new JTextField();
	private final        JTextField           jOutputFileChooser = new JTextField();
	
	private              JPanel               omeroModeLayout    = new JPanel();
	private              JTextField           jTextFieldHostname = new JTextField();
	private              JTextField           jTextFieldPort     = new JTextField();
	private              JTextField           jTextFieldUsername = new JTextField();
	private              JPasswordField       jPasswordField     = new JPasswordField();
	private              JTextField           jTextFieldGroup    = new JTextField();
	private              String[]             dataTypes          = {"Project", "Dataset", "Tag", "Image"};
	private              JComboBox            jComboBoxDataType  = new JComboBox(dataTypes);
	private              JTextField           jTextFieldSourceID = new JTextField();
	private              JTextField           jTextFieldOutputProject = new JTextField();
	
	private final        JPanel               configFilePanel    = new JPanel();
	private final        JLabel               defConf            = new JLabel("Default configuration");
	private final        AutocropConfigDialog autocropConfigFileDialog;
	private final        JRadioButton         rdoDefault         = new JRadioButton("Default");
	private final        JRadioButton         rdoAddConfigFile   = new JRadioButton("From file");
	private final        JTextField           jConfigFileChooser = new JTextField();
	private              JButton              confButton         = new JButton("...");;
	private final        JRadioButton         rdoAddConfigDialog = new JRadioButton("New");
	private final        JButton              jButtonConfig      = new JButton("Config");
	
	private final        JPanel               startQuitPanel;
	private              boolean              start              = false;
	private              int                  configMode         = 0;
	
	
	
	/** Architecture of the graphical windows */
	public AutocropDialog() {
		JButton jButtonStart = new JButton("Start");
		JButton jButtonQuit  = new JButton("Quit");
		this.setTitle("Autocrop NucleusJ2");
		this.setMinimumSize(new Dimension(400, 500));
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		autocropConfigFileDialog = new AutocropConfigDialog(this);
		autocropConfigFileDialog.setVisible(false);
		
		container = getContentPane();
		BoxLayout mainBoxLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		container.setLayout(mainBoxLayout);
		
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		
		// Use Omero ?
		ButtonGroup bGroupOmeroMode = new ButtonGroup();
		bGroupOmeroMode.add(omeroYesButton);
		omeroYesButton.addItemListener(this);
		bGroupOmeroMode.add(omeroNoButton);
		omeroNoButton.setSelected(true);
		omeroNoButton.addItemListener(this);
		JPanel radioOmeroPanel = new JPanel();
		radioOmeroPanel.setLayout(new BoxLayout(radioOmeroPanel, BoxLayout.X_AXIS));
		JLabel jLabelOmero = new JLabel("Select from omero :");
		radioOmeroPanel.add(jLabelOmero);
		radioOmeroPanel.add(omeroYesButton);
		radioOmeroPanel.add(omeroNoButton);
		radioOmeroPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		container.add(radioOmeroPanel, 0);
		
		
		// Local mode layout
		localModeLayout.setLayout(new BoxLayout(localModeLayout, BoxLayout.Y_AXIS));
		
		JPanel localPanel = new JPanel();
		GridBagLayout localLayout = new GridBagLayout();
		localLayout.columnWeights = new double[] {1,5,0.5};
		localPanel.setLayout(localLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		JLabel jLabelInput = new JLabel("Input directory:");
		localPanel.add(jLabelInput, c);
		c.gridx = 1;
		c.insets = new Insets(0,0,0,20);
		localPanel.add(jInputFileChooser, c);
		jInputFileChooser.setMaximumSize(new Dimension(10000,20));
		JButton sourceButton = new JButton("...");
		sourceButton.addActionListener(this);
		sourceButton.setName(INPUT_CHOOSER);
		c.insets = new Insets(0,0,0,0);;
		c.gridx = 2;
		localPanel.add(sourceButton, c);

		JLabel jLabelOutput = new JLabel("Output directory :");
		c.gridx = 0;
		c.gridy = 1;
		localPanel.add(jLabelOutput, c);
		c.gridx = 1;
		c.insets = new Insets(0,0,0,20);
		localPanel.add(jOutputFileChooser, c);
		jOutputFileChooser.setMaximumSize(new Dimension(10000,20));
		JButton destButton = new JButton("...");
		destButton.addActionListener(this);
		destButton.setName(OUTPUT_CHOOSER);
		c.insets = new Insets(10,0,0,0);
		c.gridx = 2;
		localPanel.add(destButton, c);
		
		localPanel.setBorder(padding);
		localModeLayout.add(localPanel);
		container.add(localModeLayout, 1);
		
		
		// Omero mode layout
		omeroModeLayout.setLayout(new BoxLayout(omeroModeLayout, BoxLayout.Y_AXIS));
		
		JPanel omeroPanel = new JPanel();
		GridBagLayout omeroLayout = new GridBagLayout();
		omeroLayout.columnWeights = new double[] {0.1, 0.1, 2};
		omeroPanel.setLayout(omeroLayout);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5,0,5,20);
		
		c.gridy = 0;
		JLabel jLabelHostname = new JLabel("Hostname :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelHostname,c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldHostname, c);
		jTextFieldHostname.setMaximumSize(new Dimension(10000,20));

		c.gridy = 1;
		JLabel jLabelPort = new JLabel("Port :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelPort,c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldPort,c);
		jTextFieldPort.setMaximumSize(new Dimension(10000,20));

		c.gridy = 2;
		JLabel jLabelUsername = new JLabel("Username :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelUsername,c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldUsername,c);
		jTextFieldUsername.setMaximumSize(new Dimension(10000,20));
		
		c.gridy = 3;
		JLabel jLabelPassword = new JLabel("Password :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelPassword,c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jPasswordField,c);
		jPasswordField.setMaximumSize(new Dimension(10000,20));

		c.gridy = 4;
		JLabel jLabelGroup = new JLabel("Group ID :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelGroup, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldGroup, c);
		jTextFieldGroup.setMaximumSize(new Dimension(10000,20));

		c.gridy = 5;
		JLabel jLabelSource = new JLabel("Source :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelSource,c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataType,c);
		c.gridx = 2;
		omeroPanel.add(jTextFieldSourceID,c);
		jTextFieldSourceID.setMaximumSize(new Dimension(10000,20));
		
		
		c.gridy = 6;
		JLabel jLabelOutputProject = new JLabel("Output project :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelOutputProject,c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldOutputProject,c);
		jTextFieldOutputProject.setMaximumSize(new Dimension(10000,20));
		
		omeroPanel.setBorder(padding);
		omeroModeLayout.add(omeroPanel);
		

		// Config panel
		JPanel configPanel = new JPanel();
		configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.X_AXIS));
		JLabel jLabelConfig = new JLabel("Config file (optional):");
		configPanel.add(jLabelConfig);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(rdoDefault);

		rdoDefault.setSelected(true);
		rdoDefault.addItemListener(this);
		configPanel.add(rdoDefault);
		
		buttonGroup.add(rdoAddConfigDialog);
		rdoAddConfigDialog.addItemListener(this);
		configPanel.add(rdoAddConfigDialog);
		
		buttonGroup.add(rdoAddConfigFile);
		rdoAddConfigFile.addItemListener(this);
		configPanel.add(rdoAddConfigFile);
		configPanel.setBorder(padding);
		container.add(configPanel, 2);
		// Initialize config to default
		container.add(defConf,3);
		defConf.setBorder(padding);
		configMode = 0;
		
		
		// Start/Quit buttons
		startQuitPanel = new JPanel();
		startQuitPanel.setLayout(new BoxLayout(startQuitPanel, BoxLayout.X_AXIS));
		startQuitPanel.add(jButtonStart);
		startQuitPanel.add(jButtonQuit);
		startQuitPanel.setBorder(padding);
		container.add(startQuitPanel,4);
		
		QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		jButtonStart.addActionListener(startListener);
		ConfigListener configListener = new ConfigListener(this);
		jButtonConfig.addActionListener(configListener);
		
		this.setVisible(true);
	}
	
	
	public String getInput() {
		return jInputFileChooser.getText();
	}
	public String getOutput() {
		return jOutputFileChooser.getText();
	}
	
	public boolean isOmeroEnabled(){ return useOmero; }
	
	public String getHostname(){ return jTextFieldHostname.getText(); }
	public String getPort(){ return jTextFieldPort.getText(); }
	public String getSource(){ return jTextFieldSourceID.getText(); }
	public String getDataType(){ return (String) jComboBoxDataType.getSelectedItem(); }
	public String getUsername(){ return jTextFieldUsername.getText(); }
	public String getPassword(){ return String.valueOf(jPasswordField.getPassword()); }
	public String getGroup(){ return jTextFieldGroup.getText(); }
	public String getOutputProject(){ return jTextFieldOutputProject.getText(); }
	
	public String getConfig() { return jConfigFileChooser.getText(); }
	public int getConfigMode() { return configMode; }
	public AutocropConfigDialog getAutocropConfigFileDialog() { return autocropConfigFileDialog; }
	public boolean isStart() { return start; }
	
	
	public void actionPerformed(ActionEvent e) {
		switch (((JButton) e.getSource()).getName()) {
			case INPUT_CHOOSER:
				fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				break;
			case OUTPUT_CHOOSER:
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				break;
			case CONFIG_CHOOSER:
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				break;
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			switch (((JButton) e.getSource()).getName()) {
				case INPUT_CHOOSER:
					File selectedInput = fc.getSelectedFile();
					jInputFileChooser.setText(selectedInput.getPath());
					break;
				case OUTPUT_CHOOSER:
					File selectedOutput = fc.getSelectedFile();
					jOutputFileChooser.setText(selectedOutput.getPath());
					break;
				case CONFIG_CHOOSER:
					File selectedConfig = fc.getSelectedFile();
					jConfigFileChooser.setText(selectedConfig.getPath());
					break;
			}
		}
		fc.setSelectedFile(null);
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();

		if(source == omeroNoButton){
			container.remove(1);
			container.add(localModeLayout, 1);
			useOmero = false;
		}
		else if(source == omeroYesButton){
			container.remove(1);
			container.add(omeroModeLayout, 1);
			useOmero = true;
		}
		else{
			container.remove(3);
			if (autocropConfigFileDialog.isVisible()) autocropConfigFileDialog.setVisible(false);

			Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
			if (source == rdoDefault) {
				container.add(defConf,3);
				defConf.setBorder(padding);
				configMode = 0;
			} else if (source == rdoAddConfigDialog) {
				container.add(jButtonConfig, 3);
				jButtonConfig.setBorder(padding);
				configMode = 1;
			} else if (source == rdoAddConfigFile) {
				configFilePanel.setLayout(new BoxLayout(configFilePanel, BoxLayout.X_AXIS));
				configFilePanel.add(jConfigFileChooser);
				jConfigFileChooser.setMaximumSize(new Dimension(10000,20));
				confButton.addActionListener(this);
				confButton.setName(CONFIG_CHOOSER);
				configFilePanel.add(confButton);
				container.add(configFilePanel, 3);
				configFilePanel.setBorder(padding);
				configMode = 2;
			}
		}
		
		validate();
		repaint();
	}
	
	
	/** Classes listener to interact with the several elements of the window */
	
	class StartListener implements ActionListener {
		final AutocropDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public StartListener(AutocropDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			autocropDialog.dispose();
			autocropConfigFileDialog.dispose();
		}
		
	}
	
	class QuitListener implements ActionListener {
		final AutocropDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public QuitListener(AutocropDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropDialog.dispose();
			autocropConfigFileDialog.dispose();
		}
		
	}
	
	class ConfigListener implements ActionListener {
		final AutocropDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public ConfigListener(AutocropDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropConfigFileDialog.setVisible(true);
		}
		
	}
	
}
