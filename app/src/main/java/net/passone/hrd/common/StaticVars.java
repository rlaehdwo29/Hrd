package net.passone.hrd.common;

import java.util.ArrayList;

import net.passone.hrd.container.CurriculumItem;
import net.passone.hrd.container.LectureItem;
import net.passone.hrd.container.SiteItem;




public class StaticVars {

	/**
	 * Temporary
	 */
	
	public static String uid = null;
//	

	public static ArrayList<SiteItem> siteItems 
		= new ArrayList<SiteItem>();
	public static ArrayList<CurriculumItem> curriItems = new ArrayList<CurriculumItem>();
	public static ArrayList<LectureItem> lectureItems = new ArrayList<LectureItem>();

	public static void clearStaticVars() {
		// ȸ���ڵ� (����)
		uid = null;
		curriItems.clear();
		lectureItems.clear();
		siteItems.clear();

	}	
}
