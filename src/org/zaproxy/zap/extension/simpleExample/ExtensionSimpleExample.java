/*
 * Zed Attack Proxy (ZAP) and its related class files.
 * 
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package org.zaproxy.zap.extension.simpleExample;

import java.awt.CardLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JTextPane;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.extension.ExtensionAdaptor;
import org.parosproxy.paros.extension.ExtensionHook;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.view.ZapMenuItem;

/*
 * An example ZAP extension which adds a top level menu item. 
 * 
 * This class is defines the extension.
 */
public class ExtensionSimpleExample extends ExtensionAdaptor {

	// The name is public so that other extensions can access it
	public static final String NAME = "ExtensionSimpleExample";
	
	// The i18n prefix, by default the package name - defined in one place to make it easier
	// to copy and change this example
	protected static final String PREFIX = "simpleExample";

	private static final String RESOURCE = "/org/zaproxy/zap/extension/simpleExample/resources";
	
	private static final ImageIcon ICON = new ImageIcon(
			ExtensionSimpleExample.class.getResource( RESOURCE + "/cake.png"));

	private static final String EXAMPLE_FILE = "example/ExampleFile.txt";
	
    private ZapMenuItem menuExample = null;
	private RightClickMsgMenu popupMsgMenuExample = null;
	private AbstractPanel statusPanel = null;

    private Logger log = Logger.getLogger(this.getClass());

	/**
     * 
     */
    public ExtensionSimpleExample() {
        super();
 		initialize();
    }

    /**
     * @param name
     */
    public ExtensionSimpleExample(String name) {
        super(name);
    }

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        this.setName(NAME);
	}
	
	@Override
	public void hook(ExtensionHook extensionHook) {
	    super.hook(extensionHook);
	    
	    if (getView() != null) {
	    	// Register our top menu item, as long as we're not running as a daemon
	    	// Use one of the other methods to add to a different menu list
	        extensionHook.getHookMenu().addToolsMenuItem(getMenuExample());
	    	// Register our popup menu item
	    	extensionHook.getHookMenu().addPopupMenuItem(getPopupMsgMenuExample());
	    	// Register a 
	    	extensionHook.getHookView().addStatusPanel(getStatusPanel());
	    }

	}

	private AbstractPanel getStatusPanel() {
		if (statusPanel == null) {
			statusPanel = new AbstractPanel();
			statusPanel.setLayout(new CardLayout());
	        statusPanel.setName(Constant.messages.getString(PREFIX + ".panel.title"));
	        statusPanel.setIcon(ICON);
	        JTextPane pane = new JTextPane();
			pane.setEditable(false);
			pane.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 12));
			pane.setContentType("text/html");
			pane.setText(Constant.messages.getString(PREFIX + ".panel.msg"));
			statusPanel.add(pane);
		}
		return statusPanel;
	}

	private ZapMenuItem getMenuExample() {
        if (menuExample == null) {
        	menuExample = new ZapMenuItem(PREFIX + ".topmenu.tools.title");

        	menuExample.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent ae) {
            		// This is where you do what you want to do.
            		// In this case we'll just show a popup message.
            		View.getSingleton().showMessageDialog(
            				Constant.messages.getString(PREFIX + ".topmenu.tools.msg"));
            		// And display a file included with the add-on in the Output tab
            		displayFile(EXAMPLE_FILE);
                }
            });
        }
        return menuExample;
    }
	
	private void displayFile (String file) {
		if (! View.isInitialised()) {
			// Running in daemon mode, shouldnt have been called
			return;
		}
		try {
			File f = new File(Constant.getZapHome(), file);
			if (! f.exists()) {
				// This is something the user should know, so show a warning dialog
				View.getSingleton().showWarningDialog(
						MessageFormat.format(
								Constant.messages.getString(ExtensionSimpleExample.PREFIX + ".error.nofile"),
								f.getAbsolutePath()));
				return;
			}
			// Quick way to read a small text file
			String contents = new String(Files.readAllBytes(f.toPath()));
			// Write to the output panel
			View.getSingleton().getOutputPanel().append(contents);
			// Give focus to the Output tab
			View.getSingleton().getOutputPanel().setTabFocus();
		} catch (Exception e) {
			// Something unexpected went wrong, write the error to the log
			log.error(e.getMessage(), e);
		}
	}

	private RightClickMsgMenu getPopupMsgMenuExample() {
		if (popupMsgMenuExample  == null) {
			popupMsgMenuExample = new RightClickMsgMenu(this, 
					Constant.messages.getString(PREFIX + ".popup.title"));
		}
		return popupMsgMenuExample;
	}
	@Override
	public String getAuthor() {
		return Constant.ZAP_TEAM;
	}

	@Override
	public String getDescription() {
		return Constant.messages.getString(PREFIX + ".desc");
	}

	@Override
	public URL getURL() {
		try {
			return new URL(Constant.ZAP_EXTENSIONS_PAGE);
		} catch (MalformedURLException e) {
			return null;
		}
	}
}