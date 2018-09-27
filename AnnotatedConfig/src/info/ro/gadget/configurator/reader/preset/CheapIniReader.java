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
 * プリセット用。ごくごく単純なiniファイルのリーダです。<br>
 * 下記の機能をサポートしています<br>
 * ・=による値の記述
 * ・;によるコメントの記述
 * ・[セクション]によるセクション分割読み込み
 * ・複数回記述による複数パラメータ読み込み（配列系パラメータのみ）
 * ・カンマ区切りによる複数パラメータ読み込み
 * 下記の機能は「チープ」の名がつく通りサポートしていません。
 * ・各種エスケープシーケンス
 * ・値の複数行にわたる記述
 * @author Robert_Ordis
 *
 */
public class CheapIniReader implements ConfigReader{

	private static final Pattern sectionPattern = Pattern.compile("^\\s*\\[([^\\]]+)\\].*$");	//セクション判別用
	
	@Override
	public String getDomainSeparator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSplitter() {
		// TODO Auto-generated method stub
		return ",";
	}
	
	@Override
	public String getCommentStart() {
		// TODO Auto-generated method stub
		return ";";
	}
	
	@Override
	public Set<String> readConfig(String path, String section, ConfigLineListener listener) throws IOException {
		// TODO Auto-generated method stub
		File srcFile = new File(path);
		return this.readConfig(srcFile, section, listener);
	}
	
	public Set<String> readConfig(File file, String section, ConfigLineListener listener) throws IOException{
		String line;
		FileReader fr = new FileReader(file);
		BufferedReader br = null;
		Set<String> ret = new HashSet<String>();
		boolean inSection = section.equals("");
		try {
			br = new BufferedReader(fr);
			int i = 0;
			while((line = br.readLine())!=null){					//複数行にまたがるコンフィグはない前提。
				//まず、「セクション」に当てはまるかどうかを探知する。
				i++;
				Matcher m = sectionPattern.matcher(line);
				if(m.find()) {
					String nextSection = m.group(1).trim();
					inSection = section.equals(nextSection);
					//ここでは階層的読み込みはサポートしないため、空白セクション（＝ルート）でのみ次のセクションを示す。
					if(section.equals("")) {
						System.out.println("next section:["+nextSection+"]");
						ret.add(nextSection.trim());
					}
					continue;
				}
				if(!inSection) {
					continue;
				}
				//現在いるセクションでの読み込み。
				String[] strs = line.split(this.getCommentStart(), 2);
				String cstr = strs[0];
				String comment = strs.length < 2 ? "":strs[1];
				String pName = "", pValue = "";
				
				strs = cstr.split("=", 2);
				pName = strs[0];
				if(strs.length > 1) {
					//値ありの場合。
					pValue = strs[1];
				}
				
				listener.onRead(i, pName.trim(), pValue.trim(), comment);
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
