package info.ro.gadget.configurator.reader.preset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.ro.gadget.configurator.reader.ConfigLineListener;
import info.ro.gadget.configurator.reader.ConfigReader;

/**
 * 自作のチープなpropertiesのリーダ。
 * propertiesの仕様にはある程度倣うけど、独自拡張もちょいちょい入るのです。
 * wikipediaに書いてあること基本的に守ろうとしてみてます。
 * ただし、配列値への入力に関してはカンマ区切りを実施しています。
 * @author Robert_Ordis
 *
 */
public class CheapPropertiesReader implements ConfigReader{

	private static final Pattern commentPattern = Pattern.compile("^\\s*[#!](.*)$");	//コメント行判別用
	private static final Pattern multiLinedPattern = Pattern.compile("^(.*)\\\\$");		//複数行のパターン
	private static final Pattern unicodeSplitted = Pattern.compile("^([0-9a-fA-F]{4})(.*)");
	@Override
	public String getDomainSeparator() {
		// TODO Auto-generated method stub
		return ".";
	}

	@Override
	public String getSplitter() {
		// TODO Auto-generated method stub
		//return "(?<!\\\\),";
		return ",";
	}

	@Override
	public String getCommentStart() {
		// TODO Auto-generated method stub
		return "#";
	}

	@Override
	public Set<String> readConfig(String path, String section, ConfigLineListener listener) throws IOException {
		// TODO Auto-generated method stub
		File srcFile = new File(path);
		return this.readConfig(srcFile, section, listener);
	}
	
	private String decodeEscape(String src) {
		String ret = src.replaceAll("\\\\r", "")
				.replaceAll("\\\\n", System.getProperty("line.separator"))
				.replaceAll("\\\\t", "\t")
				.replaceAll("\\\\ ", " ");
		StringBuilder sb = new StringBuilder();
		String[] ar = ret.split("\\\\u");
		sb.append(ar[0]);
		int hiCode = 0x0000;
		for(int i = 1; i < ar.length; i++) {
			//"\\u"で分割。数字４つが頭に来たならそれをunicodeに変換する。
			//System.out.println("index["+i+"]:"+ar[i]);
			Matcher m = unicodeSplitted.matcher(ar[i]);
			//System.out.println(m);
			//System.out.println(m.groupCount());
			if(m.find()) {
				//int code = Integer.valueOf(m.group(1), 16);
				int code = Integer.valueOf(ar[i].substring(0, 4), 16);
				String behinds = ar[i].substring(4);
				int[] codes = null;
				if(0xD800 <= code && code < 0xDC00) {
					//サロゲートペア上位→後を取ると空文字列になるはずなのでそれ含めて検証。
					//System.out.println("hi surrogate");
					if(hiCode == 0 && "".equals(behinds)) {
						hiCode = code;
						continue;
					}
					//不正なシーケンスで上位サロゲートに来たら変換対象外。
					sb.append("\\u"+ar[i]);
					continue;
				}
				if(0xDC00 <= code && code < 0xE000) {
					//サロゲートペア下位→上位と組み合わせて文字を作る。
					//System.out.println("lo surrogate");
					if(hiCode != 0) {
						codes = new int[2];
						codes[0] = hiCode;
						codes[1] = code;
						hiCode = 0;
					}
					else {
						//不正なシーケンスで下位サロゲートに来たら変換対象外。
						sb.append("\\u"+ar[i]);
						continue;
					}
				}
				
				if(codes == null) {
					codes = new int[1];
					codes[0] = code;
				}
				sb.append(new String(codes, 0, codes.length));
				sb.append(behinds);
				continue;
			}
			//変換対象外
			if(hiCode != 0) {
				sb.append("\\u"+Integer.toHexString(hiCode));
				hiCode = 0;
			}
			sb.append("\\u"+ar[i]);
		}
		return sb.toString();
	}
	
	public Set<String> readConfig(File file, String section, ConfigLineListener listener) throws IOException{
		String line;
		StringBuilder commentBuilder = new StringBuilder();
		FileReader fr = new FileReader(file);
		BufferedReader br = null;
		Set<String> ret = new HashSet<String>();
		try {
			br = new BufferedReader(fr);
			int i = 0;
			while((line = br.readLine())!=null){
				//まず、「コメント行」に当てはまるかどうかを探知する。空白のみも無視。
				i++;
				Matcher m = commentPattern.matcher(line);
				if(m.find()) {
					//コメント行。
					commentBuilder.append(this.decodeEscape(m.group(1)));
					commentBuilder.append("\n");
					continue;
				}
				if(line.trim().equals("")) {
					//空白のみも無視。
					continue;
				}
				
				//現在いるセクションでの読み込み。
				String comment = commentBuilder.toString();
				commentBuilder = new StringBuilder();
				String pName = "", pValue = "";
				
				//行末に'\'を検出したら次の行まで内容はつながっていることを示す。
				StringBuilder lineBuilder = new StringBuilder();
				String read = line;
				while(true) {
					m = multiLinedPattern.matcher(read);
					if(m.find()) {
						lineBuilder.append(m.group(1).replaceAll("^\\s*", ""));
					}
					else {
						lineBuilder.append(read.replaceAll("^\\s*", ""));
						break;
					}
					if((read = br.readLine()) == null) {
						break;
					}
					i++;
				}
				line = lineBuilder.toString();
				String[] strs = line.split("[=:]|($<![=:])a", 2);
				if(strs.length < 2) {
					strs = line.split("($!\\\\)\\s", 2);
				}
				pName = strs[0];
				//trimの代わり。先頭は普通に削除するけど、
				pName = pName.replaceAll("^\\s*", "");
				pName = pName.replaceAll("((?!\\\\)\\s)*$", "");
				pName = this.decodeEscape(pName);
				if(strs.length > 1) {
					//値ありの場合。
					pValue = strs[1];
				}
				
				String finValue = pValue.replaceAll("^\\s*", "");
				
				//次に、名前の記述がセクション（今回はサブドメイン内かどうか）に入ってることを検証する。
				if(section != null && !"".equals(section)) {
					//ルートなら特に何もしないけどサブなら何かやる。
					String prefix = section+this.getDomainSeparator();
					if(!pName.startsWith(prefix)) {
						//サブとしての要件（指定されたセクションに入ってない）ということだとＮＧ。
						continue;
					}
					//サブドメイン内のパラメータ名で通知する。
					pName = pName.substring(prefix.length());
				}
				
				//セクション内に入ってることが確認できたので続けていく。
				finValue = this.decodeEscape(finValue);
				//System.out.println(finValue);
				listener.onRead(i, pName, finValue, comment);
				
				//サブドメイン候補をつけていく。
				strs = pName.split("\\.");
				if(strs.length > 1) {
					ret.add(strs[0]);
				}
			}
		}
		finally {
			if(br != null) {
				br.close();
			}
			fr.close();
		}
		return ret;
	}
}
