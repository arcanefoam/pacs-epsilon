package org.eclipse.epsilon.evl.incremental.orientdb.dialog;

import org.eclipse.epsilon.common.incremental.dt.launching.dialogs.AbstractTraceManagerConfigurationDialog;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class OrientDBManagerConfiguration extends AbstractTraceManagerConfigurationDialog {

	public static final String DB_URL = "url";

	public static final String DB_USER = "user";

	public static final String DB_PASS = "pass";

	public static final String DB_CREATE = "create";
	
	private Text databaseUrlText;
	private Text userText;
	private Text passwordText;

	@Override
	protected String getExecutionTraceManagerName() {
		return "OrientDB Trace Manager";
	}
	
	@Override
	protected void loadProperties() {
		if (properties == null) return;
		databaseUrlText.setText(properties.getProperty(DB_URL));
		// FIXME add a save credentials button (safety risk)
		userText.setText(properties.getProperty(DB_USER));
		passwordText.setText(properties.getProperty(DB_PASS));
	}

	@Override
	protected void storeProperties() {
		properties = new StringProperties();
		properties.put(DB_URL, databaseUrlText.getText());
		properties.put(DB_USER, userText.getText());
		properties.put(DB_PASS, passwordText.getText());
		// FIXME Add check button for this
		properties.put(DB_CREATE, true);
	}
	
	@Override
	protected void createGroups(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "DB information", 2);
		Label databaseUriLabel = new Label(groupContent, SWT.NONE);
		databaseUriLabel.setText("DataBase URI: ");
		databaseUrlText = new Text(groupContent, SWT.BORDER);
		databaseUrlText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		Label userLabel = new Label(groupContent, SWT.NONE);
		userLabel.setText("user: ");
		userText = new Text(groupContent, SWT.BORDER);
		userText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label passwordLabel = new Label(groupContent, SWT.NONE);
		passwordLabel.setText("password: ");
		passwordText = new Text(groupContent, SWT.BORDER);
		passwordText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
	}

}
