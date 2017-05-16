package net.passone.hrd.adapter;

import java.util.List;

import net.passone.hrd.common.StaticVars;
import net.passone.hrd.container.CurriculumInfo;
import net.passone.hrd.container.CurriculumItem;
import net.passone.hrd.container.LectureInfo;
import net.passone.hrd.container.LectureItem;
import net.passone.hrd.container.Site;
import net.passone.hrd.container.SiteItem;
import android.util.Log;


public class AdapterItemManager {
	private static void site_addlist(String siteid, String sitename) {
    	SiteItem siteitem = new SiteItem();    	
    	siteitem.setSite(siteid, sitename);
    	StaticVars.siteItems.add(siteitem);
    }
	private static void curri_addlist(String curridx, String orderby,String subject, String leccount, String fileurl) {
    	CurriculumItem ctem = new CurriculumItem();    	
    	ctem.setCurriculum(curridx, orderby, subject, leccount, fileurl);
    	StaticVars.curriItems.add(ctem);
    }
	private static void lec_addlist(String part, String page,String subject, String readflag, String movieurl, String htmlurl,String contentskey,String cdnurl) {
    	LectureItem ltem = new LectureItem();    	
    	ltem.setLecture(part, page, subject, readflag, movieurl, htmlurl,contentskey,cdnurl);
    	StaticVars.lectureItems.add(ltem);
    }
	public static void AddSite(List<Site> siteList) {
		StaticVars.siteItems.clear();
		for (Site s : siteList) {
			Log.d("passone",s.siteid);
			AdapterItemManager.site_addlist(s.siteid, s.sitename);
		}	     
	}
	public static void AddCurriculum(List<CurriculumInfo> clist) {
		StaticVars.curriItems.clear();
		for (CurriculumInfo c : clist) {
			AdapterItemManager.curri_addlist(c.curridx, c.orderby, c.subject, c.leccount, c.fileurl);
		}	     
	}
	public static void AddLecture(List<LectureInfo> leclist) {
		StaticVars.lectureItems.clear();
		for (LectureInfo l : leclist) {

			AdapterItemManager.lec_addlist(l.part, l.page, l.subject, l.readflag, l.movieurl, l.htmlurl,l.contentskey,l.cdeurl);
		}	     
	}
}