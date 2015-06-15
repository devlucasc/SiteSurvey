package br.com.site.survey;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.ImageIcon;

public class NewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private File imageFile;
	private Image loadedImage;
	private JTextField textField_3;
	public File getImageFile() {
		return imageFile;
	}

	public double getImageWidth() {
		if(loadedImage != null) {
			return loadedImage.getWidth(null);
		}
		return -1.0f;
	}

	public double getImageHeight() {
		if(loadedImage != null) {
			return loadedImage.getHeight(null);
		}
		return -1.0f;
	}

	public File loadFile() {
		JFileChooser file = new JFileChooser();
		FileFilter mainFilter = new FileNameExtensionFilter("Image File (*.jpeg; *.jpg; *.jfif; *.bmp; *.png)",
				"jpeg", "jpg", "jfif", "bmp", "png");
		FileFilter filter1 = new FileNameExtensionFilter("Jpeg File (*.jpg; *.jpeg; *jfif)","jpg", "jpeg", "jfif");
		FileFilter filter2 = new FileNameExtensionFilter("Bmp File (*.bmp)", "bmp");
		FileFilter filter3 = new FileNameExtensionFilter("Png File (*.png)", "png");
		file.setDialogTitle("Choose file to open...");
		file.removeChoosableFileFilter(file.getFileFilter());
		file.addChoosableFileFilter(filter1);
		file.addChoosableFileFilter(filter2);
		file.addChoosableFileFilter(filter3);
		file.setFileFilter(mainFilter);
		file.setMultiSelectionEnabled(false);
		int i= file.showOpenDialog(this);
		if (i!=1){
			if(ImageTool.isValidImage(file.getSelectedFile())) {
				loadedImage = ImageTool.loadImage(file.getSelectedFile());
				return file.getSelectedFile();
			}
		}
		return null;
	}
	
	public double getInputWidth() {
		return Double.parseDouble(textField.getText().replace(',', '.'));
	}
	
	public double getInputHeight() {
		return Double.parseDouble(textField_1.getText().replace(',', '.'));
	}
	
	public double getBetaParam() {
		return Double.parseDouble(textField_3.getText().replace(',', '.'));
	}

	public Point2D getScale() {
		Point2D result = new Point2D.Double(-1, -1);
		if(loadedImage != null) {
			result.setLocation((this.getInputWidth() / this.getImageWidth()),
					(this.getInputHeight() / this.getImageHeight()));
		}
		return result;
	}

	/**
	 * Create the dialog.
	 */
	public NewDialog() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setModal(true);
		setTitle("New...");
		setType(Type.UTILITY);
		setResizable(false);
		setBounds(100, 100, 288, 145);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.UNRELATED_GAP_COLSPEC,
				ColumnSpec.decode("60px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("60px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("20px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("60px"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblFloorSize = new JLabel("Floor Size:");
			contentPanel.add(lblFloorSize, "2, 1, left, center");
		}
		{
			textField = new JNumberFormatField(new DecimalFormat("0.00"));
			textField.setHorizontalAlignment(JTextField.RIGHT);
			contentPanel.add(textField, "4, 1, right, default");
			textField.setColumns(10);
		}
		{
			JLabel lblM = new JLabel("m");
			contentPanel.add(lblM, "6, 1");
		}
		{
			JLabel lblX = new JLabel("X");
			contentPanel.add(lblX, "8, 1, center, default");
		}
		{
			textField_1 = new JNumberFormatField(new DecimalFormat("0.00"));
			textField_1.setHorizontalAlignment(JTextField.RIGHT);
			contentPanel.add(textField_1, "10, 1, left, default");
			textField_1.setColumns(10);
		}
		{
			JLabel lblM_1 = new JLabel("m");
			contentPanel.add(lblM_1, "12, 1");
		}
		{
			JLabel lblImageFile = new JLabel("Image File:");
			contentPanel.add(lblImageFile, "2, 2, right, default");
		}
		{
			textField_2 = new JTextField();
			textField_2.setEditable(false);
			contentPanel.add(textField_2, "4, 2, fill, default");
			textField_2.setColumns(10);
		}
		{
			JButton button = new JButton("");
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					imageFile = loadFile();
					if(imageFile != null) {
						try {
							textField_2.setText(imageFile.getCanonicalPath());
						} catch (IOException e1) {
						}
					}
				}
			});
			button.setIcon(new ImageIcon(NewDialog.class.getResource("/icons/open.png")));
			contentPanel.add(button, "8, 2");
		}
		{
			JLabel lblBeta = new JLabel("\u03B2 Param:");
			contentPanel.add(lblBeta, "2, 3, right, default");
		}
		{
			textField_3 = new JNumberFormatField(new DecimalFormat("0.00"));
			textField_3.setHorizontalAlignment(JTextField.RIGHT);
			contentPanel.add(textField_3, "4, 3, fill, default");
			textField_3.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				InputMap im = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "closeOnEsc");
				ActionMap am = this.getRootPane().getActionMap();
				am.put("closeOnEsc", new AbstractAction() {
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						cancelButton.doClick();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
		//pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

}
