package com.rubbertranslator.modules.translate.youdao;

import java.util.List;
import com.google.gson.annotations.SerializedName;

/**
 * 有道APi请求参数：
 * q	text	待翻译文本	True	必须是UTF-8编码
 * from	text	源语言	True	参考下方 支持语言 (可设置为auto)
 * to	text	目标语言	True	参考下方 支持语言 (可设置为auto)
 * appKey	text	应用ID	True	可在 应用管理 查看
 * salt	text	UUID	True	UUID
 * sign	text	签名	True	sha256(应用ID+input+salt+curtime+应用密钥)
 * signType	text	签名类型	True	v3
 * curtime	text	当前UTC时间戳(秒)	true	TimeStamp
 * ext	text	翻译结果音频格式，支持mp3	false	mp3
 * voice	text	翻译结果发音选择	false	0为女声，1为男声。默认为女声
 * strict	text	是否严格按照指定from和to进行翻译：true/false	false	如果为false，则会自动中译英，英译中。默认为false
 * -------------------------------------------------------------------------------
 * 响应
 *
 * errorCode	text	错误返回码	一定存在
 * query	text	源语言	查询正确时，一定存在
 * translation	Array	翻译结果	查询正确时，一定存在
 * basic	text	词义	基本词典，查词时才有
 * web	Array	词义	网络释义，该结果不一定存在
 * l	text	源语言和目标语言	一定存在
 * dict	text	词典deeplink	查询语种为支持语言时，存在
 * webdict	text	webdeeplink	查询语种为支持语言时，存在
 * tSpeakUrl	text	翻译结果发音地址	翻译成功一定存在，需要应用绑定语音合成实例才能正常播放
 * 否则返回110错误码
 * speakUrl	text	源语言发音地址	翻译成功一定存在，需要应用绑定语音合成实例才能正常播放
 * 否则返回110错误码
 * returnPhrase	Array	单词校验后的结果	主要校验字母大小写、单词前含符号、中文简繁体
 *
 * 英文查词
 * us-phonetic	美式音标，英文查词成功，一定存在
 * phonetic	默认音标，默认是英式音标，英文查词成功，一定存在
 * uk-phonetic	英式音标，英文查词成功，一定存在
 * uk-speech	英式发音，英文查词成功，一定存在
 * us-speech	美式发音，英文查词成功，一定存在
 * explains	基本释义
 */

public class YoudaoTranslationResult{

	@SerializedName("returnPhrase")
	private List<String> returnPhrase;

	@SerializedName("web")
	private List<WebItem> web;
	
	@SerializedName("basic")
	private Basic basic;
	
	@SerializedName("tSpeakUrl")
	private String tSpeakUrl;

	@SerializedName("RequestId")
	private String requestId;

	@SerializedName("query")
	private String query;

	@SerializedName("translation")
	private List<String> translation;

	@SerializedName("errorCode")
	private String errorCode;

	@SerializedName("dict")
	private Dict dict;

	@SerializedName("webdict")
	private Webdict webdict;

	@SerializedName("l")
	private String L;

	@SerializedName("speakUrl")
	private String speakUrl;


	public String getTSpeakUrl(){
		return tSpeakUrl;
	}

	public String getRequestId(){
		return requestId;
	}

	public String getQuery(){
		return query;
	}

	public List<String> getTranslation(){
		return translation;
	}

	public String getErrorCode(){
		return errorCode;
	}

	public Dict getDict(){
		return dict;
	}

	public Webdict getWebdict(){
		return webdict;
	}

	public String getL(){
		return L;
	}

	public String getSpeakUrl(){
		return speakUrl;
	}

	public List<WebItem> getWeb(){
		return web;
	}
	
	public List<String> getReturnPhrase(){
		return returnPhrase;
	}
	

	public Basic getBasic() {
		return basic;
	}

	public String gettSpeakUrl() {
		return tSpeakUrl;
	}



	@Override
	public String toString() {
		return "YoudaoTranslationResult{" +
				"returnPhrase=" + returnPhrase +
				", web=" + web +
				", basic=" + basic +
				", tSpeakUrl='" + tSpeakUrl + '\'' +
				", requestId='" + requestId + '\'' +
				", query='" + query + '\'' +
				", translation=" + translation +
				", errorCode='" + errorCode + '\'' +
				", dict=" + dict +
				", webdict=" + webdict +
				", L='" + L + '\'' +
				", speakUrl='" + speakUrl + '\'' +
				'}';
	}

	public class Webdict{

		@SerializedName("url")
		private String url;

		public String getUrl(){
			return url;
		}

		@Override
		public String toString() {
			return "Webdict{" +
					"url='" + url + '\'' +
					'}';
		}
	}

	public class Dict{

		@SerializedName("url")
		private String url;

		public String getUrl(){
			return url;
		}
	}

	public class Basic{

		@SerializedName("exam_type")
		private List<String> examType;

		@SerializedName("us-phonetic")
		private String usPhonetic;

		@SerializedName("phonetic")
		private String phonetic;

		@SerializedName("uk-phonetic")
		private String ukPhonetic;

		@SerializedName("wfs")
		private List<WfsItem> wfs;

		@SerializedName("uk-speech")
		private String ukSpeech;

		@SerializedName("explains")
		private List<String> explains;

		@SerializedName("us-speech")
		private String usSpeech;

		public List<String> getExamType(){
			return examType;
		}

		public String getUsPhonetic(){
			return usPhonetic;
		}

		public String getPhonetic(){
			return phonetic;
		}

		public String getUkPhonetic(){
			return ukPhonetic;
		}

		public List<WfsItem> getWfs(){
			return wfs;
		}

		public String getUkSpeech(){
			return ukSpeech;
		}

		public List<String> getExplains(){
			return explains;
		}

		public String getUsSpeech(){
			return usSpeech;
		}

		@Override
		public String toString() {
			return "Basic{" +
					"examType=" + examType +
					", usPhonetic='" + usPhonetic + '\'' +
					", phonetic='" + phonetic + '\'' +
					", ukPhonetic='" + ukPhonetic + '\'' +
					", wfs=" + wfs +
					", ukSpeech='" + ukSpeech + '\'' +
					", explains=" + explains +
					", usSpeech='" + usSpeech + '\'' +
					'}';
		}
	}



	public  class WebItem{

		@SerializedName("value")
		private List<String> value;

		@SerializedName("key")
		private String key;

		public List<String> getValue(){
			return value;
		}

		public String getKey(){
			return key;
		}

		@Override
		public String toString() {
			return "WebItem{" +
					"value=" + value +
					", key='" + key + '\'' +
					'}';
		}
	}

	public class Wf{

		@SerializedName("name")
		private String name;

		@SerializedName("value")
		private String value;

		public String getName(){
			return name;
		}

		public String getValue(){
			return value;
		}

		@Override
		public String toString() {
			return "Wf{" +
					"name='" + name + '\'' +
					", value='" + value + '\'' +
					'}';
		}
	}

	public class WfsItem{

		@SerializedName("wf")
		private Wf wf;

		public Wf getWf(){
			return wf;
		}

		@Override
		public String toString() {
			return "WfsItem{" +
					"wf=" + wf +
					'}';
		}
	}
}