package net.passone.hrd.common;

import java.io.UnsupportedEncodingException;

import android.text.InputFilter;
import android.text.Spanned;

public class ByteLengthFilter implements InputFilter{
		 
		  private String mCharset;
		  protected int mMaxByte;
		  public ByteLengthFilter(int maxbyte, String charset)
		  {
		     this.mMaxByte = maxbyte;
		     this.mCharset = charset;
		  }
		 
		 public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,int dend) {
		  String expected = new String();
		  expected += dest.subSequence(0, dstart);
		  expected += source.subSequence(start, end);
		  expected += dest.subSequence(dend, dest.length());
		  int keep = calculateMaxLength(expected) - (dest.length() - (dend - dstart));
		  if(keep < 0) {
		   keep = 0;
		  }
		  int Rekeep = plusMaxLength(dest.toString(), source.toString(), start);
		  
		  if (keep <= 0 && Rekeep <= 0) {
		   return "";
		   
		  }
		  else if (keep >= end - start) {
		   return null; 
		  }
		  else {
		   if( dest.length() == 0 && Rekeep <= 0 ) {
		    return source.subSequence(start, start + keep); 
		   }
		   else if(Rekeep <= 0) {
		    return source.subSequence(start, start + (source.length()-1));
		   }
		   else {
		    return source.subSequence(start, start + Rekeep); 
		   }
		  }
		 }
		 protected int plusMaxLength( String expected, String source, int start ) {
		  int keep = source.length();
		  int maxByte = mMaxByte - getByteLength(expected.toString()); 
		  
		  while (getByteLength(source.subSequence(start, start + keep).toString()) > maxByte) {
		   keep--;
		  };
		  return keep;
		 }
		 
		 protected int calculateMaxLength(String expected) {
		  int expectedByte = getByteLength(expected);
		  if (expectedByte == 0) {
		   return 0;
		  }
		  return mMaxByte - (getByteLength(expected) - expected.length());
		 }
		 private int getByteLength(String str) {
		  try {
		   return str.getBytes(mCharset).length;
		  } catch (UnsupportedEncodingException e) {
		  
		  }
		  return 0;
		 }
}
