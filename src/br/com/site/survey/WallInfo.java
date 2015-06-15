package br.com.site.survey;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import java.awt.GridLayout;

import javax.swing.JTextField;

/**
 * Wall Dialog Class
 * @author Lucas Corsi
 *
 */
public class WallInfo extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JComboBox<String> comboBox;
	private double length;
	private MaterialType mType;

	/**
	 * Create the dialog.
	 */
	public WallInfo(JFrame parent, String lengthStr) {
		super(parent, ModalityType.APPLICATION_MODAL);
		setType(Type.UTILITY);
		setResizable(false);
		setBounds(100, 100, 237, 116);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		{
			JLabel lblMaterialType = new JLabel("Material Type");
			contentPanel.add(lblMaterialType);
		}
		comboBox = new JComboBox<String>();
		contentPanel.add(comboBox);
		{
			JLabel lblNewLabel = new JLabel("Length (m)");
			contentPanel.add(lblNewLabel);
		}
		{
			textField = new JTextField();
			textField.setEditable(false);
			textField.setHorizontalAlignment(JTextField.RIGHT);
			textField.setText(lengthStr);
			//textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			contentPanel.add(textField);
			textField.setColumns(10);
		}
		{
			for (int i = 0; i < MaterialType.values().length; i++) {
				comboBox.addItem(MaterialType.values()[i].getDescription());
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try{
							Double d = Double.parseDouble(textField.getText().replace(',', '.'));
							setLength(d.doubleValue());
						}catch(Exception e2){
							setLength(-1.0f);
						}
						int selIndex = comboBox.getSelectedIndex();
						if(selIndex >= 0) {
							String desc = comboBox.getItemAt(selIndex);
							for (int i = 0; i < MaterialType.values().length; i++) {
								if(MaterialType.values()[i].getDescription().equals(desc)) {
									setmType(MaterialType.values()[i]);
									break;
								}
							}
						}
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
						setLength(-1.0f);
						setmType(null);
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

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public MaterialType getmType() {
		return mType;
	}

	public void setmType(MaterialType mType) {
		this.mType = mType;
	}

}
