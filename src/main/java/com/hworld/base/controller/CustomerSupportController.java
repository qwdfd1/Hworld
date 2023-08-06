package com.hworld.base.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.hworld.base.service.CustomerSupportService;
import com.hworld.base.util.Pager;
import com.hworld.base.vo.BoardVO;
import com.hworld.base.vo.NoticeVO;
import com.hworld.base.vo.QnaVO;

import lombok.extern.slf4j.Slf4j;


@Controller
@RequestMapping("/cs/*")
@Slf4j
public class CustomerSupportController {
	
	@Autowired
	private CustomerSupportService csService;
	
	// 고객지원
	@GetMapping("home")
	public ModelAndView c1() throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("hworld/customerSupport");
		return modelAndView;
	}
	
	// ----------------------------------------------------------------------------------------------------
	// 공지사항
	@GetMapping("notice")
	public ModelAndView getNoticeList(Pager pager) throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		
		List<BoardVO> list = csService.getNoticeList(pager);
		log.info("공지사항 리스트 사이즈 =====================> {}", list.size());
		modelAndView.setViewName("hworld/notice");
		modelAndView.addObject("list", list);
		return modelAndView;
	}
	
	@GetMapping("noticeDetail")
	public ModelAndView getNoticeDetail(NoticeVO noticeVO, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		Cookie [] Cookies = request.getCookies();
		
		Cookie newCookie = null;
		
		if(Cookies != null && Cookies.length > 0) {
			for (Cookie cookie : Cookies) {
				
				if(cookie.getName().equals("notice"+noticeVO.getNum())) {
					newCookie = cookie;
				}
			}
		}
		
		if(newCookie == null) {
			newCookie = new Cookie("notice"+noticeVO.getNum(), "|" + noticeVO.getNum() + "|");
			newCookie.setMaxAge(60*60*24);
			newCookie.setPath("/");
			csService.setBoardHitUpdate(noticeVO);
			response.addCookie(newCookie);
		}

		mv.addObject("vo", csService.getNoticeDetail(noticeVO));
		mv.addObject("board", "notice");
		mv.setViewName("hworld/noticeDetail");
		return mv;
	}
	
	@GetMapping("noticeAdd")
	public ModelAndView setNoticeAdd() throws Exception {
		ModelAndView mv = new ModelAndView();
		
		mv.addObject("board", "notice");
		mv.setViewName("hworld/noticeAdd");
		return mv;
	}
	
	@PostMapping("noticeAdd")
	public ModelAndView setNoticeAdd(NoticeVO noticeVO, String board, HttpSession session, MultipartFile file) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		String msg = "공지사항 등록 실패";
		
		int result = csService.setAdd(noticeVO, board, session, file);
		
		if(result > 0) {
			msg = "공지사항 등록 성공";
		}
		
		mv.addObject("result", msg);
		mv.addObject("url", "./notice");
		
		mv.setViewName("common/result");
		return mv;
	}
	
	@GetMapping("noticeUpdate")
	public ModelAndView setNoticeUpdate(NoticeVO noticeVO) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		noticeVO = (NoticeVO)csService.getNoticeDetail(noticeVO);
		log.error("NOTICE CHECK : ------------------> {}", noticeVO.getNoticeCheck());
		
		mv.addObject("vo", csService.getNoticeDetail(noticeVO));
		mv.addObject("board", "notice");
		mv.setViewName("hworld/noticeUpdate");
		return mv;
	}
	
	// ----------------------------------------------------------------------------------------------------
	// 1:1 문의
	@GetMapping("qna")
	public ModelAndView setQnaAdd(HttpSession session) throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("hworld/qna");
		modelAndView.addObject("list", csService.setAdd(session));
		return modelAndView;
	}
	
	@PostMapping("qna")
	public ModelAndView setQnaAdd(QnaVO qnaVO, HttpSession session, MultipartFile file) throws Exception {
		
		ModelAndView mv = new ModelAndView();
		
		String msg = "문의 등록 실패";
		
		int result = csService.setAdd(qnaVO, session, file);
		
		if(result > 0)  {
			msg = "문의 등록 성공";
		}
		
		mv.addObject("result", msg);
		mv.addObject("url", "./qna");
			
		
		mv.setViewName("common/result");
		
		return mv;
		
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	// 신청서/자료실
	@GetMapping("archive")
	public ModelAndView c4() throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("hworld/archive");
		return modelAndView;
	}
	
	
	// ----------------------------------------------------------------------------------------------------
	// 파일다운
	@GetMapping("fileDown")
	public ModelAndView getFileDown(String filePath, String board) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		mv.addObject("filePath", filePath);
		mv.addObject("board", board);
		mv.setViewName("boardFileManager");
		
		return mv;
	
	}
	

}
