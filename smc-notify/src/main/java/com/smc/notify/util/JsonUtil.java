package com.smc.notify.util;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class JsonUtil {
	private static ThreadLocal<StringBuilder> local = new ThreadLocal<StringBuilder>();

	private static Map<Class, BeanInfo> beanInfoMap = Collections.synchronizedMap(new WeakHashMap<Class, BeanInfo>());

	public static String jsonCharFilter(String sourceStr) {
		if (sourceStr == null) {
			return "";
		}
		sourceStr = sourceStr.replace("\n", "");
		sourceStr = sourceStr.replace("\r", "");
		return sourceStr;
	}

    /**
     * JSONObject 自动转成xml string<BR>
     * <P>
     * 说明：<BR>
     * 此方法可以将JSON对象自动转换成xml字符串，用于在controller层需要json和xml两种方式返回数据时用.<BR>
     * 目前各平台都在逐步使用json格式接口,所以<B>推荐使用先获取json, 再把json转成xml</B><BR>
     * <P>
     * 优点:<BR>
     * 属性和数组语义清晰,不会出现像xml转json时, 属性与数组混乱的情况.<BR>
     * 对于像<,>,[,],{,}等特殊符号,如果它的存在不影响json结构, 转换成xml时会自动转义. <BR>
     * 可以支持空属性了(空白字符), 保持了每个数据对象的完整性. <BR>
     * null属性容错, 如果对象的某个属性为null,则会忽略对应的属性但不会报错. <BR>
     * <P>
     * 目前发现的缺点:<BR>
     * 转换的xml中,element的顺序是按字典排序的,与json原来的顺序不一致, 会影响阅读效果.<BR>
     * 数组下面的element节点的名称只能统一用一个名称(都叫item, 但不会影响解析)<BR>
     * @param json
     * @return
     */
	public static String getXmlFromJson(JSONObject json) {
		XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setRootName("root"); // 根节点的名称为root
        xmlSerializer.setTypeHintsEnabled(false); // 不在xml标签中添加element的值的类型描述
        xmlSerializer.setElementName("item"); // 所有数组下的元素的标签名都叫item
		//xmlSerializer.setArrayName("list");
		String xmlString = xmlSerializer.write(json);
		return xmlString;
	}

    /**
     * JSON string 自动转xml string
     * @param jsonStr
     * @return
     */
	public static String getXmlFromJson(String jsonStr) {
		JSONObject json = JSONObject.fromObject(jsonStr);
		return getXmlFromJson(json);
	}

    /**
     * 将xml字符串转换为JSON对象
     *
     * @param xmlString
     *            xml字符串
     * @return JSON对象
     */
	public static JSON getJSONFromXml(String xmlString) {
		XMLSerializer xmlSerializer = new XMLSerializer();
		//xmlSerializer.setForceTopLevelObject(true);
        xmlSerializer.setSkipWhitespace(true); // element下面只有一个element时将自动转数组. (语义不清晰,容易出错)
        // 应该在element结点上增加class属性明确告诉转换器这个是数组array(或者是对象object), 见XMLSerializer的源代码的getClass方法
        // eg <foucs class="array"><news>1</news></focus>  表示数组, 里面的news将是数组里的一个元素, 转换为json将是  [news]
        // 如果不加 class=array,  则news将是focus下面的一个子对象, 转换为json是:  {news:1}
        //xmlSerializer.setTrimSpaces(true); // 是否trim()空白字符
		//xmlSerializer.setTypeHintsEnabled(false);
		JSON json = xmlSerializer.read(xmlString);
		return json;
	}

	public static void main(String[] args) {
        String xml = "<root><focus class=\"array\"><news><id>11</id><name>我的stlye也还可以饿啊   \n      部不错哦哦</name></news></focus><guide><news>2</news></guide></root>";
		//	System.out.println(getJSONFromXml(xml));
		//JSONArray.fromObject(getJSONFromXml(xml)).get(0);
		//JSONObject.fromObject(getJSONFromXml(xml)).getJSONObject("focus");//.getJSONObject(0);
        //	xml = getXmlFromJson("{\"content-encoding\":\"UTF-8\",\"resultList\":[{\"id\":\"1\",\"refId\":\"\\131]\",\"refType\":\"10\",\"link\":\"news://<subId.>=131\",\"title\":\"西藏商报\",\"utime\":\"0\",\"abstrac\":\"追随时代步伐 传递社会信息\"},{\"id\":\"2\",\"refId\":\"4521328\",\"refType\":\"1\",\"link\":\"news://newsId=4521328\",\"title\":\"胡春华(图)\",\"utime\":\"0\"},{\"id\":\"3\",\"refId\":\"4521215\",\"refType\":\"1\",\"link\":\"news://newsId=4521215\",\"title\":\"换肝人邢登清渴望重返西藏(组图)\",\"utime\":\"0\"},{\"id\":\"4\",\"refId\":\"4520829\",\"refType\":\"1\",\"link\":\"news://newsId=4520829\",\"title\":\"江苏援藏前方指挥部成立\",\"utime\":\"0\"},{\"id\":\"5\",\"refId\":\"4520362\",\"refType\":\"1\",\"link\":\"news://newsId=4520362\",\"title\":\"“十二五”第二批风电项目获批风电概念股再获政策支持\",\"utime\":\"0\"},{\"id\":\"6\",\"refId\":\"4520308\",\"refType\":\"1\",\"link\":\"news://newsId=4520308\",\"title\":\"胡春华任广东省委书记汪洋卸任致函感谢网友(组图)\",\"utime\":\"0\"},{\"id\":\"7\",\"refId\":\"4520250\",\"refType\":\"1\",\"link\":\"news://newsId=4520250\",\"title\":\"歌诗图跨界体验之旅拉萨收官\",\"utime\":\"0\"},{\"id\":\"8\",\"refId\":\"4519958\",\"refType\":\"1\",\"link\":\"news://newsId=4519958\",\"title\":\"今年医药招聘供不应求\",\"utime\":\"0\"},{\"id\":\"9\",\"refId\":\"4519211\",\"refType\":\"1\",\"link\":\"news://newsId=4519211\",\"title\":\"以良好党风带政风促民风\",\"utime\":\"0\"},{\"id\":\"10\",\"refId\":\"4518384\",\"refType\":\"1\",\"link\":\"news://newsId=4518384\",\"title\":\"浦城古寺挖出千年“书架”(组图)\",\"utime\":\"0\"},{\"id\":\"11\",\"refId\":\"4517828\",\"refType\":\"1\",\"link\":\"news://newsId=4517828\",\"title\":\"每日好节目荟萃荧屏间\",\"utime\":\"0\"},{\"id\":\"12\",\"refId\":\"4517608\",\"refType\":\"1\",\"link\":\"news://newsId=4517608\",\"title\":\"羽泉“正能量”席卷网络\",\"utime\":\"0\"},{\"id\":\"13\",\"refId\":\"4517053\",\"refType\":\"1\",\"link\":\"news://newsId=4517053\",\"title\":\"中央任命五省区新书记(组图)\",\"utime\":\"0\"},{\"id\":\"14\",\"refId\":\"4516578\",\"refType\":\"1\",\"link\":\"news://newsId=4516578\",\"title\":\"胡春华简历(图)\",\"utime\":\"0\"},{\"id\":\"15\",\"refId\":\"4516134\",\"refType\":\"1\",\"link\":\"news://newsId=4516134\",\"title\":\"18大后履新省部级高官：均具掌控复杂局面能力\",\"utime\":\"1355875425000\",\"abstrac\":\"原标题：十八大后10省份调整党委书记    昨日，浙江省委书记夏宝龙、陕西省委书记赵正永、广东省委书记胡春华、吉林省委书记王儒林、内蒙古自治区党委书\"}],\"totalCount\":\"15\"}");
		System.out.println(xml);

		//xml = xml.replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "");

        String regex = "[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]"; // 多行时存在问题
		xml = Pattern.compile(regex).matcher(xml).replaceAll("");

		System.out.println(xml);

	}

    /**
     * 将xmlDocument转换为JSON对象
     *
     * @param xmlDocument
     *            XML Document
     * @return JSON对象
     */
	public static JSON getJSONFromXml(Document xmlDocument) {
		String xmlString = xmlDocument.toString();
		return getJSONFromXml(xmlString);
	}

    /**
     * 将xml字符串转换为JSON字符串
     *
     * @param xmlString
     * @return JSON字符串
     */
	public static String getJSONStringFromXml(String xmlString) {
		return getJSONFromXml(xmlString).toString();
	}

    /**
     * 将Java对象转换为JSON格式的字符串
     *
     * @param javaObj
     *            POJO,例如日志的model
     * @return JSON格式的String字符串
     */
	public static String getJsonStringFromJavaPOJO(Object javaObj) {
		return JSONObject.fromObject(javaObj).toString(1);
	}

    /**
     * 将JavaBen，其属性包含集合、map的复杂对象转换成JSON字符串；
     *
     * @param obj
     * @return
     */

	public static String getJsonStringFromObject(Object obj) {
		fromObject(obj);
		StringBuilder builder = builder();
		local.remove();

		return builder.toString();
	}

	private static StringBuilder builder() {
		StringBuilder builder = local.get();
		if (builder == null) {
			builder = new StringBuilder();
			local.set(builder);
		}
		return builder;
	}

	private static void fromObject(Object obj) {
		if (obj == null) {
			builder().append("null");
		} else if (obj instanceof String) {
			builder().append(quote((String) obj));
		} else if (obj instanceof Number || obj instanceof Character || obj instanceof Boolean) {
			fromPrimitive(obj);
		} else if (obj instanceof Date) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = format.format((Date) obj);
			builder().append("\"").append(date).append("\"");
		} else if (obj.getClass().isArray()) {
			fromArray(obj);
		} else if (obj instanceof Collection) {
			fromCollection((Collection) obj);
		} else if (obj instanceof Map) {
			fromMap((Map) obj);
		} else {
			fromBean(obj);
		}
	}

	private static void fromPrimitive(Object obj) {
		if (obj instanceof Character) {
			Character c = (Character) obj;
			char[] carr = { c };
			builder().append(quote(new String(carr)));
		} else {
			builder().append(obj);
		}
	}

    // 该方法拷贝自net.sf.json.util.JSONUtils
	private static String quote(String string) {
		char b;
		char c = 0;
		int i;
		int len = string.length();
		StringBuffer sb = new StringBuffer(len * 2);
		String t;
		char[] chars = string.toCharArray();
		char[] buffer = new char[1030];
		int bufferIndex = 0;
		sb.append('"');
		for (i = 0; i < len; i += 1) {
			if (bufferIndex > 1024) {
				sb.append(buffer, 0, bufferIndex);
				bufferIndex = 0;
			}
			b = c;
			c = chars[i];
			switch (c) {
			case '\\':
			case '"':
				buffer[bufferIndex++] = '\\';
				buffer[bufferIndex++] = c;
				break;
			case '/':
				if (b == '<') {
					buffer[bufferIndex++] = '\\';
				}
				buffer[bufferIndex++] = c;
				break;
			default:
				if (c < ' ') {
					switch (c) {
					case '\b':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'b';
						break;
					case '\t':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 't';
						break;
					case '\n':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'n';
						break;
					case '\f':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'f';
						break;
					case '\r':
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'r';
						break;
					default:
						t = "000" + Integer.toHexString(c);
						int tLength = t.length();
						buffer[bufferIndex++] = '\\';
						buffer[bufferIndex++] = 'u';
						buffer[bufferIndex++] = t.charAt(tLength - 4);
						buffer[bufferIndex++] = t.charAt(tLength - 3);
						buffer[bufferIndex++] = t.charAt(tLength - 2);
						buffer[bufferIndex++] = t.charAt(tLength - 1);
					}
				} else {
					buffer[bufferIndex++] = c;
				}
			}
		}
		sb.append(buffer, 0, bufferIndex);
		sb.append('"');
		return sb.toString();
	}

	private static void fromArray(Object array) {
		StringBuilder builder = builder();
		builder.append("[");
		Class type = array.getClass().getComponentType();
		if (!type.isPrimitive()) {
			Object[] objArr = (Object[]) array;
			for (int i = 0; i < objArr.length; i++) {
				fromObject(objArr[i]);
				if (i != (objArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Boolean.TYPE) {
			boolean[] boolArr = (boolean[]) array;
			for (int i = 0; i < boolArr.length; i++) {
				builder.append(boolArr[i]);
				if (i != (boolArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Byte.TYPE) {
			byte[] byteArr = (byte[]) array;
			for (int i = 0; i < byteArr.length; i++) {
				builder.append(byteArr[i]);
				if (i != (byteArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Short.TYPE) {
			short[] shortArr = (short[]) array;
			for (int i = 0; i < shortArr.length; i++) {
				builder.append(shortArr[i]);
				if (i != (shortArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Integer.TYPE) {
			int[] intArr = (int[]) array;
			for (int i = 0; i < intArr.length; i++) {
				builder.append(intArr[i]);
				if (i != (intArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Long.TYPE) {
			long[] longArr = (long[]) array;
			for (int i = 0; i < longArr.length; i++) {
				builder.append(longArr[i]);
				if (i != (longArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Float.TYPE) {
			float[] floatArr = (float[]) array;
			for (int i = 0; i < floatArr.length; i++) {
				builder.append(floatArr[i]);
				if (i != (floatArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Double.TYPE) {
			double[] doubleArr = (double[]) array;
			for (int i = 0; i < doubleArr.length; i++) {
				builder.append(doubleArr[i]);
				if (i != (doubleArr.length - 1)) {
					builder.append(",");
				}
			}
		} else if (type == Character.TYPE) {
			char[] charArr = (char[]) array;
			for (int i = 0; i < charArr.length; i++) {
				char[] carr = { charArr[i] };
				builder.append(quote(new String(carr)));
				if (i != (charArr.length - 1)) {
					builder.append(",");
				}
			}
		}
		builder.append("]");
	}

	private static void fromCollection(Collection coll) {
		StringBuilder builder = builder();
		builder.append("[");
		Iterator iterator = coll.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			fromObject(obj);
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("]");
	}

	private static void fromMap(Map map) {
		StringBuilder builder = builder();
		builder.append("{");
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			Object key = iterator.next();
			builder.append("\"").append(key).append("\":");
			fromObject(map.get(key));
			if (iterator.hasNext()) {
				builder.append(",");
			}
		}
		builder.append("}");
	}

	private static void fromBean(Object bean) {
		StringBuilder builder = builder();
		builder.append("{");

		try {
			// BeanInfo beanInfo = getBeanInfo(bean.getClass(), bean
			// .getClass().getSuperclass());
			BeanInfo beanInfo = getBeanInfo(bean.getClass(), Object.class);

			PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < props.length; i++) {
				PropertyDescriptor pdesc = props[i];
				String pname = pdesc.getName();
				Object[] args = {};
				Object pvalue = pdesc.getReadMethod().invoke(bean, args);
				if (pvalue == null) {
					if (i == (props.length - 1)) {
						builder.deleteCharAt(builder.length() - 1);
					}
					continue;
				}
				builder.append("\"").append(pname).append("\":");
				fromObject(pvalue);
				if (i != (props.length - 1)) {
					builder.append(",");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		builder.append("}");
	}

	private static BeanInfo getBeanInfo(Class beanClass, Class stopClass) throws IntrospectionException {
		BeanInfo beanInfo = beanInfoMap.get(beanClass);
		if (beanInfo == null) {
			beanInfo = Introspector.getBeanInfo(beanClass, stopClass);
			beanInfoMap.put(beanClass, beanInfo);
		}

		return beanInfo;
	}

    /**
     * 将json串转换为一个map对象
     * @param jsonString
     * @return
     */
	public static Map<String,Object> getMapFromJson(String jsonString) {
        if(StringUtils.isEmpty(jsonString)){
        	return null;
        }
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
        Iterator<Object> keyIter = jsonObject.keys();
        String key;
        Object value;
        Map<String,Object> valueMap = new HashMap<String,Object>();

        while (keyIter.hasNext()) {
            key = (String) keyIter.next();
            value = jsonObject.get(key);
            valueMap.put(key, value);
        }

        return valueMap;
    }

    /**获取json对象中特定域的值 */
    public static String getStringValue(JSONObject jsonObj, String field) {
        if (null != jsonObj && jsonObj.has(field)) {
            return jsonObj.getString(field);
        }
        return null;
    }

    /**向json对象中设置特定域的值*/
    public static void setFieldValue(JSONObject jsonObj, String field, Object value) {
        if (null != jsonObj && null != value) {
            if (value instanceof String && StringUtils.isNotBlank(String.valueOf(value))) {
                jsonObj.put(field, value);
            } else {
                jsonObj.put(field, value);
            }
        }
    }

    /**
     * 获取json对象中特定域的Int值
     *
     * @throws org.json.JSONException
     */
    public static Integer getIntValue(JSONObject jsonObj, String field,Integer defaultVal){
        try{
            if (null != jsonObj && jsonObj.has(field) && null!=jsonObj.get(field)) {
                String val = jsonObj.getString(field);
                if (StringUtils.isNotBlank(val)) {
                    return Integer.valueOf(val);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return defaultVal;
    }
}
