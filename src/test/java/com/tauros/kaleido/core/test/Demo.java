package com.tauros.kaleido.core.test;

import com.tauros.kaleido.core.cache.KaleidoCache;
import com.tauros.kaleido.core.cache.SizeUnit;
import com.tauros.kaleido.core.util.KaleidoCodec;
import org.apache.commons.codec.binary.Base64;

import java.io.Serializable;


/**
 * Created by tauros on 2016/4/9.
 */
public class Demo implements Serializable {
	private static final long serialVersionUID = -2777120453873339539L;

	static KaleidoCache<String, byte[]> cache = new KaleidoCache<>(SizeUnit.MEGABYTES, 100, (obj) -> obj.length);

	public static void main(String[] args) throws Exception {
		String str = "\n" +
		             "時光守護者 宅宅（JJ）林時光守護者 宅宅（JJ）林林俊杰1135549\n" +
		             "今天7点半解说下B组LTZvsAI今天7点半解说下B组LTZvsAI伍声2009204388\n" +
		             "大家好，我叫de yi ao大家好，我叫de yi aoZhou陈尧42135\n" +
		             "Axx：需要你，我是一直鱼，欧巴Axx：需要你，我是一直鱼，欧巴阿夏夏12017\n" +
		             "Fish小飞 对黑训练。Fish小飞 对黑训练。小飞Lcr 7193\n" +
		             "解说创世传说杯解说创世传说杯小蝴蝶6289\n" +
		             "SGC职业联赛的直播间SGC职业联赛的直播间SGC职业联赛2391\n" +
		             "朴弟（朴一生)-暴力美学只玩输出位朴弟（朴一生)-暴力美学只玩输出位朴弟1593\n" +
		             "子阳~   队伍人齐  跟书神训练！子阳~ 队伍人齐 跟书神训练！_子阳1024\n" +
		             "old boy眼袋侠带节奏old boy眼袋侠带节奏大叔小智677\n" +
		             "燕青  训练燕青 训练燕青sama665\n" +
		             "DOTA2主播开黑DOTA2主播开黑First天津饭458\n" +
		             "肖璐 训练 对面情书队肖璐 训练 对面情书队aixiaolu282\n" +
		             "闭麦听歌，1V9开始！闭麦听歌，1V9开始！九门大提督Zz267\n" +
		             "输一局  群里红包福利一波！！！输一局 群里红包福利一波！！！自定义旋律262\n" +
		             "为了房租！fighting！为了房租！fighting！月神AresKing261\n" +
		             "✿励志做游戏主播的娱乐主播✿励志做游戏主播的娱乐主播丸子是个球144\n" +
		             "慢慢：15岁天才少年冲6000慢慢：15岁天才少年冲6000_慢慢86\n" +
		             "IMBA期待被虐 包鸟眼的寂寞谁能懂IMBA期待被虐 包鸟眼的寂寞谁能懂汨罗张学友72\n" +
		             "武陵阿姨的首播~人数到了跳舞走起来A";

		String str2 = KaleidoCodec.Base64.NORMAL.encode(str);
		System.out.println(str2);

		System.out.println(KaleidoCodec.Base64.NORMAL.decode(str2));
	}
}
