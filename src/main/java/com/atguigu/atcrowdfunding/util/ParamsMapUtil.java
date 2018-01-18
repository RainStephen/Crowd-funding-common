package com.atguigu.atcrowdfunding.util;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * @author Administrator
 * 很遗憾的是这种方法无法防止SQL注入的问题
 * 
 * 		让 分页查询：
 * 				简单到你不能呼吸 、
 * 				高效 	：(就一个方法搞定，能不高效！！！)、
 * 				易拓展 ：想添加条件，只要编写jsp页面即可。where 语句我都帮你写好了
 * 				易用 	 ：一用就知道了
 * 
 * 
 * 优点：
		1、不需要在mybatis的sql语句中任何转义【CDATA】和判断，java程序已经处理了。
		2、自动拼接 where ，sql中不需要写 where 关键字 例如
			select * from 表名  ${map.where} ;
				注  ： ${map.where} ：查询条件拼接的  where条件  sql语句 
		3、容易拓展，需要添加条件查询时，只要在页面中按照规定格式写即可，无需考虑mybatis的sql语句
		4、查询日期条件的 非常方便！
		   	只要在jsp页面按照  name="search_createDate@yyyy-MM-dd_xxxXX"格式
			这得看你具体的日期格式，是 yy 还是 MM 还是  dd，灵活使用，比如要查某个月份@MM即可
		5、几乎ORACLE所有的类型都适用，除了二进制数据类型。
 * 
 * 功能：
 * 		可以将jsp页面发送来的请求的查询参数的map集合 map，
 * 	（一般是WebUtils.getParametersStartingWith(request, "xxx_")方法获取的map集合。）
 * 		通过调用  本类中 paramsToWhere(Map<String,Object> map) ,  
 * 		转换为sql语句中   where 语句，
 * 		例如：在mybatis的xml文件中,转换成如下
 * 		select 字段1、字段2、、  from 表名    #{where}
 * 		如果还想分页，则在XxxMapper类中需要传入一个集合，
 * 		存放分页要使用的参数startIndex,endIndex和该类转换成  的where语句  ，
 * 		在mybatis中可以这样取：用${map.where}这种方式取，例如
 * 		select * from 表名 ${map.where}
			
			
		注意：1、因为mysql和oracle处理日期不同，所以不查询日期的话都适用，
				查询日期的话，只适用oracle
				查询日期在jsp页面中得指定格式,需要加上'@',@后面跟上日期格式
				  例如：@yyyy-mm-dd HH24:mi:ss
				  查询数值型和字符串都不需要添加 @
				你可以指定查询年月日时分秒中的任意几项都可，比如查询在 大于某个月某日的
				<input type="text" name="前缀_GT@mm-dd_crateDate" />
				
			2、关于jsp页面查询条件字段  name属性的值的格式和表中字段的格式的对应规则
				当表中字段名有下滑线时       name属性中要改为驼峰命名
					create_date  ===》createDate
				不用考虑实体类中字段如何命名。
			<input type="text" name="前缀_LIKE_crateDate" 
			
			
			附加：如果要实现跳转条件回显，也很简单 在jsp表单中加上下面的value
 * 			value="${存放查询条件的map集合.关键字_查询字段 }"/>
 * 
 * 			分页查询 ，呵呵，简单到不能呼吸！！！
 * 			
 */
@Deprecated
public class ParamsMapUtil {
	
	/*
	 * 以下定义了JSP页面中的查询关键字的常量
	 * 比如：search_LIKE_username
	 */
	//包含
	public static final String LIKE = "LIKE";
	//等于
	public static final String EQ = "EQ";
	//大于
	public static final String GT = "GT";
	//大于等于
	public static final String GE = "GE";
	//小于
	public static final String LT = "LT";
	//小于等于
	public static final String LE = "LE";
	//不等于
	public static final String NOTEQ = "NOTEQ";
	
	//以下是sql语句中的关键字
	public static final String AND = " AND ";
	//Sql中  WHERE 关键字
	public static final String WHERE = " WHERE ";
	//名字中的分隔符
	public static final String SEP = "_";
	
	
	
	//定义一个Map集合存放上述常量和SQL语句中的对应关系
	public static Map<String,String> conditionMap = new HashMap();
	
	static{
		conditionMap.put(LIKE, " like ");
		conditionMap.put(EQ, " = ");
		conditionMap.put(GT, " > ");
		conditionMap.put(GE, " >= ");
		conditionMap.put(LT, " < ");
		conditionMap.put(LE, " <= ");
		conditionMap.put(NOTEQ, " != ");
	}
	
	
	
	/**
	 * 传入页面发送来的请求查询的参数集合，返回sql语句中的where语句。
	 */
	public static String  paramsToWhere(Map<String,Object> map){
		StringBuilder sb = new StringBuilder();
		sb.append(WHERE);
		if(map == null){
			return "";
		}
		//遍历表单查询条件集合
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();	//EQ@yyyy-MM-dd_createDate
			Object value = entry.getValue();//2017-11-11
			
			//用来存放@后的字段类型的格式（日期格式yyyy-MM-dd）
			String typePattern = null;	
			
			if(value !=null && value!=""){
				//因为接受的都是字符串，这里 将字符串类型的值去除两边空格
				if(value instanceof String){
					value = ((String)value).trim();
				}
				
				String keyWord = key.substring(0,key.indexOf(SEP));//EQ@yyyy-MM-dd
				System.out.println(" 刚截取的"+keyWord);
				int index = keyWord.indexOf("@");  //算出@的索引
				
				if(index !=-1){	//当有@时，说明表明了类型，现在只能是日期类型(下面句话不能换位置)
					System.out.println("keyWord "+keyWord+"index "+index);						typePattern = keyWord.substring(index+1);//yyyy-MM-dd
					keyWord = keyWord.substring(0, index);//EQ
				}
				
				//遍历查询类型
				for (Map.Entry<String, String> conEntry : conditionMap.entrySet()) {
					String key2 = conEntry.getKey();	//EQ
					String value2 = conEntry.getValue();	// =
					//判断是什么条件
					if(key2.equalsIgnoreCase(keyWord)){
						String newKey = key.substring(key.indexOf(SEP)+1);	//createDate
						
						//按照驼峰命名法，如果有大写字母说明在表中的字段是不同的，转为下划线。
						Pattern pattern = Pattern.compile("[A-Z]");
						for(int i =0;i<newKey.length();i++){
							String s = newKey.charAt(i)+"";
							if(pattern.matcher(s).matches() ){
								newKey = newKey.replace(s, SEP+s.toLowerCase());//create_date
							}
						}
						String newVal;	//新的值
						//看条件是比较大小还是like关键字
						if(key2.equals(LIKE)){
							newVal = value2+"'%"+value+"%'";
						}else{
							newVal = value2 +value;		// = '2017-11-11'
						}
						//to_char(create_date,'yyyy-mm-dd')
						if(typePattern != null){		//说明是特殊类型（日期）
							newKey = "to_char("+newKey+",'"+typePattern+"')";
							newVal = value2 +"'"+value+"'";	
						}
						sb.append(newKey).append(newVal).append(AND);
						break;
					}
				}
			}
		}
		String st = sb.toString();
		//如果sb中没有添加任何条件语句，则返回空
		if(WHERE.equals(st)){
			return "";
		}
		return  st.substring(0, st.lastIndexOf(AND));
	}
	
	

	
	
	/**
	 * 将map集合中的数据转为链接中的参数形式
	 * pre:给集合的键加上前缀
	 * 用于给页面跳转连接用
	 */
	public static String mapToString(Map<String,Object> map,String pre){
		
		if(map ==null){
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			if(value !=null && value!=""){
				sb.append(pre+key+"="+value+"&");
			}
		}
		String string = sb.toString();
		int index = string.lastIndexOf("&");
		if(string.lastIndexOf("&")!= -1){
			string =  string.substring(0,index);
		}
		return string;
	}
	
	

}
