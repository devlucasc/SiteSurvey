package br.com.site.survey;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.UIManager;

public class MainWindow {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					//System.setProperty("sun.awt.noerasebackground", "true");
					window.frmSitesurvey.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private JFrame frmSitesurvey;
	private ImagePanel imagePanel;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSitesurvey = new JFrame();
		frmSitesurvey.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

			}
		});
		frmSitesurvey.setTitle("SiteSurvey");
		frmSitesurvey.setBounds(100, 100, 678, 372);
		frmSitesurvey.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JToolBar toolBar = new JToolBar();
		frmSitesurvey.getContentPane().add(toolBar, BorderLayout.NORTH);

		JButton btnNew = new JButton("New");
		JButton button_2 = new JButton("");
		JButton button_3 = new JButton("");
		JButton btnHmap = new JButton("Show heatmap");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NewDialog diag = new NewDialog();
				if(diag.getBetaParam() > 0 && diag.getImageFile() != null &&
						diag.getImageFile().exists() && diag.getImageFile().isFile() &&
						diag.getImageHeight() > 0 && diag.getImageWidth() > 0) {
					imagePanel.newSchema();
					imagePanel.setBetaParam(diag.getBetaParam());
					imagePanel.setImgWidth((int)diag.getImageWidth());
					imagePanel.setImgHeight((int)diag.getImageHeight());
					//imagePanel.setSize(new Dimension((int) imagePanel.getImgWidth(), (int)imagePanel.getImgHeight()));
					loadFloorMapFile(diag.getImageFile());
					imagePanel.setScale(diag.getScale());
					diag.dispose();
					button_2.setEnabled(true);
					button_3.setEnabled(true);
					btnHmap.setEnabled(true);
				}
			}
		});
		btnNew.setMnemonic((int) 'N');
		btnNew.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/new.png")));
		toolBar.add(btnNew);
		toolBar.addSeparator();

		JButton button = new JButton("");
		button.setToolTipText("Undo");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(imagePanel != null) {
					imagePanel.undoWall();
				}
			}
		});
		button.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/Undo-icon.png")));
		toolBar.add(button);

		JButton button_1 = new JButton("");
		button_1.setToolTipText("Redo");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(imagePanel != null) {
					imagePanel.redoWall();
				}	
			}
		});
		button_1.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/Redo-icon.png")));
		toolBar.add(button_1);
		toolBar.addSeparator();

		button_3.setToolTipText("Insert omnidirectional Antenna");
		button_2.setEnabled(false);
		button_3.setEnabled(false);
		btnHmap.setEnabled(false);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(imagePanel != null) {
					if(button_2.getModel().isSelected()) {
						imagePanel.disableDrawWall();
						button_2.getModel().setSelected(false);
					} else {
						if(button_3.getModel().isSelected()) {
							button_3.doClick();
						}
						imagePanel.activeDrawWall();
						button_2.getModel().setSelected(true);
					}
				}
			}
		});
		button_2.setToolTipText("Add wall");
		button_2.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/wall__plus.png")));
		toolBar.add(button_2);

		button_3.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/RadioTransmitterAntennaTower.png")));
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(imagePanel != null) {
					if(button_3.getModel().isSelected()) {
						imagePanel.disableDrawAntenna();
						button_3.getModel().setSelected(false);
					} else {
						if(button_2.getModel().isSelected()) {
							button_2.doClick();
						}
						imagePanel.setAddAntennaType(AntennaType.OMNIDIRECTIONAL);
						imagePanel.activeDrawAntenna();
						button_3.getModel().setSelected(true);
					}
				}
			}
		});
		toolBar.add(button_3);

		JMenuBar menuBar = new JMenuBar();
		frmSitesurvey.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("Help");
		menuBar.add(mnFile);

		JMenuItem mntnAbout = new JMenuItem("About");
		mntnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "SiteSurvey - Computer Engineering"+
						"\r\nAdriano Goes - Propagation Prediction\r\n\r\n"+
						"Anderson Martins\t - RA 002201200000\r\n"+
						"Lucas Corsi     \t        - RA 002201200866\r\n"+
						"Marina Miatelo  \t    - RA 002201200000\r\n"+
						"Victor          \t             - RA 002201200000", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnFile.add(mntnAbout);
		imagePanel = new ImagePanel(frmSitesurvey, button, button_1, button_2, button_3, 1.0f, 1.0f, 1.0f);

		btnHmap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnHmap.isSelected()) {
					btnHmap.getModel().setSelected(false);
					imagePanel.disableHeatLayer();
				}
				else{
					btnHmap.getModel().setSelected(true);
					imagePanel.activeHeatLayer();
				}
			}
		});
		toolBar.add(btnHmap);
		ScrollPane sp = new ScrollPane();
		sp.add( imagePanel );
		frmSitesurvey.getContentPane().add(sp, BorderLayout.CENTER );
	}

	public void loadFloorMapFile(File file) {
		Image img = null;
		if(ImageTool.isValidImage(file)) {
			img = ImageTool.loadImage(file);
		}
		imagePanel.setImage(img);
		imagePanel.repaint();
	}

}
