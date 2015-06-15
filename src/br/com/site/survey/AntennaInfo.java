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

import javax.swing.JLabel;

import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JTextField;

public class AntennaInfo extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JNumberFormatField textField;
	//private JNumberFormatField ptxPower;
	//private double ptxPowerVal;
	private double freqMhz;

	/**
	 * Create the dialog.
	 */
	public AntennaInfo(JFrame parent) {
		super(parent, ModalityType.APPLICATION_MODAL);
		setType(Type.UTILITY);
		setResizable(false);
		setBounds(100, 100, 237, 93);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setModal(true);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 0));
		/*{
			JLabel lblMaterialType = new JLabel("PTx");
			contentPanel.add(lblMaterialType);
		}
		ptxPower = new JNumberFormatField(new DecimalFormat("0.00"));*/
		//contentPanel.add(ptxPower);
		{
			JLabel lblNewLabel = new JLabel("Freq. (Mhz)");
			contentPanel.add(lblNewLabel);
		}
		{
			textField = new JNumberFormatField(new DecimalFormat("0.00"));
			textField.setHorizontalAlignment(JTextField.RIGHT);
			//textField.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			contentPanel.add(textField);
			textField.setColumns(10);
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
							//Double d2 = Double.parseDouble(ptxPower.getText().replace(',', '.'));
							setFreqMhz(d.doubleValue());
							//setPtxPowerVal(d2.doubleValue());
						}catch(Exception e2){
							setFreqMhz(-1.0f);
							//setPtxPowerVal(-1.0f);
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
						setFreqMhz(-1.0f);
						//setPtxPowerVal(-1.0f);
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

	/*public double getPtxPowerVal() {
		return ptxPowerVal;
	}*/

	/*public void setPtxPowerVal(double ptxPowerVal) {
		this.ptxPowerVal = ptxPowerVal;
	}*/

	public double getFreqMhz() {
		return freqMhz;
	}

	public void setFreqMhz(double freqMhz) {
		this.freqMhz = freqMhz;
	}
}
