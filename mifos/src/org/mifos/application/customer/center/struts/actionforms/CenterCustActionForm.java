/**

* CenterActionForm    version: 1.0



* Copyright (c) 2005-2006 Grameen Foundation USA

* 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

* All rights reserved.



* Apache License
* Copyright (c) 2005-2006 Grameen Foundation USA
*

* Licensed under the Apache License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may obtain
* a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
*

* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and limitations under the

* License.
*
* See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

* and how it is applied.

*

*/
package org.mifos.application.customer.center.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.util.helpers.ValidateMethods;
import org.mifos.application.customer.struts.actionforms.CustomerActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import java.text.DateFormat;
import org.mifos.config.Localization;
import java.util.Locale;

public class CenterCustActionForm extends CustomerActionForm{
	
	private String mfiJoiningDateDD;
	private String mfiJoiningDateMM;
	private String mfiJoiningDateYY;
	
	@Override
	public String getMfiJoiningDate() {
		if (StringUtils.isNullOrEmpty(mfiJoiningDateDD) || StringUtils.isNullOrEmpty(mfiJoiningDateMM)
			|| StringUtils.isNullOrEmpty(mfiJoiningDateYY))
			return null;
			//return super.getMfiJoiningDate();
		//kim temporarily commented out return mfiJoiningDateDD + "/" + mfiJoiningDateMM + "/" + mfiJoiningDateYY;
		String dateSeparator = Localization.getInstance().getDateSeparator();
		return mfiJoiningDateDD + dateSeparator + mfiJoiningDateMM + dateSeparator + mfiJoiningDateYY;
	}
	
	public void setMfiJoiningDate(int day, int month, int year) {
		setMfiJoiningDate(Integer.toString(day), Integer.toString(month),
				Integer.toString(year));
	}
	
	public void setMfiJoiningDate(String day, String month, String year) {
		setMfiJoiningDateDD(day);
		setMfiJoiningDateMM(month);
		setMfiJoiningDateYY(year);
	}
	
	public void setMfiJoiningDateYY(String mfiJoiningDateYY) {
		this.mfiJoiningDateYY = mfiJoiningDateYY;
	}

	public String getMfiJoiningDateYY() {
		return mfiJoiningDateYY;
	}	
	
	public void setMfiJoiningDateMM(String mfiJoiningDateMM) {
		this.mfiJoiningDateMM = mfiJoiningDateMM;
	}

	public String getMfiJoiningDateMM() {
		return mfiJoiningDateMM;
	}

	public void setMfiJoiningDateDD(String mfiJoiningDateDD) {
		this.mfiJoiningDateDD = mfiJoiningDateDD;
	}

	public String getMfiJoiningDateDD() {
		return mfiJoiningDateDD;
	}

	@Override
	protected ActionErrors validateFields(HttpServletRequest request, String method) throws ApplicationException{
		ActionErrors errors = new ActionErrors();
		if(method.equals(Methods.preview.toString())){		
			validateName(errors);
			validateLO(errors);
			validateMeeting(request, errors);
			validateMfiJoiningDate(request, errors);
			validateConfigurableMandatoryFields(request,errors,EntityType.CENTER);
			validateCustomFields(request,errors);
			validateFees(request, errors);
		}else if (method.equals(Methods.editPreview.toString())){
			CenterBO center = (CenterBO)SessionUtils.getAttribute(Constants.BUSINESS_KEY,request);
			if(center.isActive())
				validateLO(errors);
			validateConfigurableMandatoryFields(request,errors,EntityType.CENTER);
			validateCustomFields(request,errors);
		}
		return errors;
	}
	
	protected void validateMfiJoiningDate(HttpServletRequest request,
			ActionErrors errors) {
		if(ValidateMethods.isNullOrBlank(getMfiJoiningDate())){
			errors.add(CustomerConstants.MFI_JOINING_DATE_MANDATORY,
				new ActionMessage(CustomerConstants.MFI_JOINING_DATE_MANDATORY));
		}
		
		else {
			if (!DateUtils.isValidDate(getMfiJoiningDate())) {
				errors.add(CustomerConstants.INVALID_MFI_JOINING_DATE, 
					new ActionMessage(CustomerConstants.INVALID_MFI_JOINING_DATE));
			}
		}
	}
	
	@Override
	protected MeetingBO getCustomerMeeting(HttpServletRequest request)throws ApplicationException{
		return (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING,request);		
	}
}
