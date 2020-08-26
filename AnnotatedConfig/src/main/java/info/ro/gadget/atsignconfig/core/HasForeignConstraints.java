package info.ro.gadget.atsignconfig.core;

import java.util.List;


/**
 * 同タイプのコンフィグを比較して何かやりたい場合のインターフェース。<br>
 * →例えば「以前の値と比較して変更してはいけない値の変更を検出した」とか。<br>
 * →例えば「他のセクションの値と比較して重複してはいけない値を検出した」とか。<br>
 * @author Robert_Ordis
 *
 */
public interface HasForeignConstraints<T extends AtsignConfig>{
	/**
	 * 他の値と重複してはならないのに重複しているものを洗い出す
	 * @param oppos
	 * @return
	 */
	public List<String> listDuplicatedButForbidden(T oppos);
	
	/**
	 * 変更してはいけない値がどの程度変更されているかを洗い出す
	 * @param oppos
	 * @return
	 */
	public List<String> listImmutableModified(T oppos);
}
