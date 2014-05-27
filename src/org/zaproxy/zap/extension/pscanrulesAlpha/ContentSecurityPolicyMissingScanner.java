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

package org.zaproxy.zap.extension.pscanrulesAlpha;

import java.util.Vector;

import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.core.scanner.Category;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.PassiveScanThread;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;

/**
 * Content Security Policy Header Missing passive scan rule 
 * https://code.google.com/p/zaproxy/issues/detail?id=1169
 * @author kingthorin+owaspzap@gmail.com
 */
public class ContentSecurityPolicyMissingScanner extends PluginPassiveScanner{

	private static final String MESSAGE_PREFIX = "pscanalpha.contentsecuritypolicymissing.";
	private static final int PLUGIN_ID = 10038;
	
	private PassiveScanThread parent = null;
	private static Logger logger = Logger.getLogger(ContentSecurityPolicyMissingScanner.class);
	
	@Override
	public void setParent(PassiveScanThread parent) {
		this.parent = parent;
	}

	@Override
	public void scanHttpRequestSend(HttpMessage msg, int id) {
		// Only checking the response for this plugin
	}
	
	@Override
	public void scanHttpResponseReceive(HttpMessage msg, int id, Source source) {
		long start = System.currentTimeMillis();
	
		if (msg.getResponseBody().length() > 0 && msg.getResponseHeader().isText()) {
			//Get the various CSP headers
			Vector<String> cspOptions = msg.getResponseHeader().getHeaders("Content-Security-Policy");
			Vector<String> xcspOptions = msg.getResponseHeader().getHeaders("X-Content-Security-Policy");
			Vector<String> xwkcspOptions = msg.getResponseHeader().getHeaders("X-WebKit-CSP");
			//If it's not null or empty add it to a single collection
			if (xcspOptions != null && xcspOptions.isEmpty() == false) 
				cspOptions.addAll(xcspOptions);
			//If it's not null or empty add it to a single collection
			if (xwkcspOptions !=null && xwkcspOptions.isEmpty() == false) 
				cspOptions.addAll(xwkcspOptions);

			if (cspOptions == null || cspOptions.isEmpty() == true) { //Header NOT Found (was null or empty)
				Alert alert = new Alert(getPluginId(), Alert.RISK_LOW, Alert.WARNING, //PluginID, Risk, Reliability
					getName()); 
		   			alert.setDetail(
		   				getDescription(), //Description
		   				msg.getRequestHeader().getURI().toString(), //URI
		   				"",	// Param
		   				"", // Attack
		   				"", // Other info
		   				getSolution(), //Solution
		   				getReference(), //References
		   				"",	// Evidence
		   				0, // 
		   				0,	//
		   				msg); //HttpMessage
		   	parent.raiseAlert(id, alert);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("\tScan of record " + id + " took " + (System.currentTimeMillis() - start) + " ms");
	    }
	}

	@Override
	public int getPluginId() {
		/*
		 * This should be unique across all active and passive rules.
		 * The master list is http://code.google.com/p/zaproxy/source/browse/trunk/src/doc/alerts.xml
		 */
		return PLUGIN_ID;
	}
	
	public String getName(){
		return Constant.messages.getString(MESSAGE_PREFIX + "name");
	}
	
	private String getDescription() {
		return Constant.messages.getString(MESSAGE_PREFIX + "desc");
	}

	private String getSolution() {
		return Constant.messages.getString(MESSAGE_PREFIX + "soln");
	}

	private String getReference() {
		return Constant.messages.getString(MESSAGE_PREFIX + "refs");
	}
	
    public int getCategory() {
        return Category.MISC;
    }
}