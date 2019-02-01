package main.java.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
/**
 * Description			: This class defines all the object related
 * 						  functions required throughout the application.
 *
 * @version				: 1.0
 * @creation			: 06/08/2012
 * @author				: Tata Consultancy Services
 */
public class ObjectUtil {
	
	public static boolean isNotNull(Object object){
		return !isNull(object);
	}

	public static boolean isNotNullOrEmpty(String string){
		return !isNull(string) && string.length() >0;
	}
	public static boolean isNull(Object object){
		return object == null;
	}
	
	public static boolean isNullOrEmpty(String object){
		return object == null ||object.trim().length() ==0;
	}
	
	public static boolean isEmptyButNotNULL(String object){
		return object != null && object.trim().length() ==0;
	}
	
	public static boolean isNotNullEmptyArray(Object[] objArr){
		if(isNotNullArray(objArr)){
			return objArr.length >0;
		}else{
			return false;
		}
	}
	
	public static boolean isNotNullArray(Object[] objArr){
		return ! (objArr ==null);
	}
	
	public static boolean isEqual(BigInteger b1,BigInteger b2){
		if(isNotNull(b1) && isNotNull(b2)){
			return b1.compareTo(b2) ==0;
		}else{
			return false;
		}
	}
	
	public static boolean isEqual(String s1,String s2){
		if(isNotNullOrEmpty(s1) && isNotNullOrEmpty(s2)){
			return s1.equalsIgnoreCase(s2);
		}else{
			return false;
		}
	}
	
	public static BigInteger getBigIntegerVal(Boolean val){
		if (ObjectUtil.isNotNull(val)){
			if (val.booleanValue()){
				return new BigInteger("1");
			}else {
				return new BigInteger("0");
			}
		}
		
		return null;
	}
	
	public static BigDecimal getBigDecimal(String val){
		if(isNotNullOrEmpty(val)){
			NumberFormat nf = NumberFormat.getInstance();
			try {
				return new BigDecimal(nf.parse(val).toString());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
	
	public static String formatBigDecimal(BigDecimal val){
		if(isNotNull(val)){
			NumberFormat df = NumberFormat.getNumberInstance();
			df.setGroupingUsed(true);
			return df.format(val);
		}else{
			return null;
		}
		
	}
	
/*	public static void sortByLatest(List list){
		if(ObjectUtil.isNotNull(list) && list.size() >0){
			Collections.sort(list, Collections.reverseOrder(DateComparator.getInstance()));
		}
	}*/
	
	public static List getSubList(List list,int startIndex,int endIndex){
		
		if(isNotNull(list) && list.size() >0){
			if((startIndex <=list.size() && endIndex <=list.size())){
				return list.subList(startIndex, endIndex);
			}else if ((startIndex <=list.size() && endIndex >=list.size())){
				return list.subList(startIndex, list.size());
			}else{
				return Collections.EMPTY_LIST;
			}
		}else{
			return Collections.EMPTY_LIST;
		}
	}
}
