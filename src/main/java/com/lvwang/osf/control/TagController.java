package com.lvwang.osf.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lvwang.osf.model.Event;
import com.lvwang.osf.model.Tag;
import com.lvwang.osf.model.User;
import com.lvwang.osf.service.InterestService;
import com.lvwang.osf.service.TagService;
import com.lvwang.osf.util.Dic;
import com.lvwang.osf.util.Property;

@Controller
@RequestMapping("/tag")
public class TagController {

	@Autowired
	@Qualifier("tagService")
	private TagService tagService;
	
	@Autowired
	@Qualifier("interestService")
	private InterestService interestService;
	
	
	@RequestMapping("/{tag_id}")
	public ModelAndView getFeedsWithTag(@PathVariable("tag_id") int tag_id, HttpSession session) {

		ModelAndView mav = new ModelAndView();
		mav.setViewName("tag/index");
		
		Tag tag = tagService.getTagByID(tag_id);
		if(tag == null) {
			mav.setViewName("404");
		}
		
		mav.addObject("tag", tag.getTag());
		mav.addObject("id", tag.getId());
		
		User user = (User)session.getAttribute("user");
		if(user != null) {
			mav.addObject("isInterest", 
						  interestService.hasInterestInTag(user.getId(), tag_id));
			
		} else {
			mav.addObject("isInterest", false);
		}
		
		List<Event> feeds = tagService.getWithTag(tag.getTag());
		mav.addObject("feeds", feeds);
		mav.addObject("dic", new Dic());
		return mav;
	}
	/**
	 * 对某个标签感兴趣
	 */
	@ResponseBody
	@RequestMapping("/{tag_id}/interest")
	public Map<String, Object> interest(@PathVariable("tag_id") int tag_id, HttpSession session) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		interestService.interestInTag(user.getId(), tag_id);
				
		ret.put("status", Property.SUCCESS_INTEREST);
		return ret;
	}
	
	
	/**
	 * 
	 */
	@ResponseBody
	@RequestMapping("/{tag_id}/undointerest")
	public Map<String, Object> undoInterest(@PathVariable("tag_id") int tag_id, HttpSession session) {
		Map<String, Object> ret = new HashMap<String, Object>();
		
		User user = (User) session.getAttribute("user");
		interestService.undoInterestInTag(user.getId(), tag_id);
		
		ret.put("status", Property.SUCCESS_INTEREST_UNDO);
		return ret;
	}
	
	
	
}
