/*
 * Copyright 2005-2015 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import javax.annotation.Resource;

import net.shopxx.entity.Admin;
import net.shopxx.service.AdminService;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller("adminProfileController")
@RequestMapping("/admin/profile")
public class ProfileController extends BaseController {

	@Resource(name = "adminServiceImpl")
	private AdminService adminService;

	@RequestMapping(value = "/check_current_password", method = RequestMethod.GET)
	public @ResponseBody
	boolean checkCurrentPassword(String currentPassword) {
		if (StringUtils.isEmpty(currentPassword)) {
			return false;
		}
		Admin admin = adminService.getCurrent();
		return StringUtils.equals(DigestUtils.md5Hex(currentPassword), admin.getPassword());
	}

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String edit(ModelMap model) {
		model.addAttribute("admin", adminService.getCurrent());
		return "/admin/profile/edit";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(String currentPassword, String password, String email, RedirectAttributes redirectAttributes) {
		if (!isValid(Admin.class, "email", email)) {
			return ERROR_VIEW;
		}
		Admin pAdmin = adminService.getCurrent();
		if (StringUtils.isNotEmpty(currentPassword) && StringUtils.isNotEmpty(password)) {
			if (!isValid(Admin.class, "password", password)) {
				return ERROR_VIEW;
			}
			if (!StringUtils.equals(DigestUtils.md5Hex(currentPassword), pAdmin.getPassword())) {
				return ERROR_VIEW;
			}
			pAdmin.setPassword(DigestUtils.md5Hex(password));
		}
		pAdmin.setEmail(email);
		adminService.update(pAdmin);
		addFlashMessage(redirectAttributes, SUCCESS_MESSAGE);
		return "redirect:edit.jhtml";
	}

}