package cn.count.easydrive366.components;

import java.util.List;

import cn.count.easydrive366.ActivateCodeActivity.CodeInfo;
import cn.count.easydrive366.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CodeListTable extends LinearLayout {
	private LayoutInflater _inflater =null;
	private Context _context;
	private TableLayout _table;
	public CodeListTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		_inflater = LayoutInflater.from(context);
		_context = context;
		_inflater.inflate(R.layout.partview_code_table, this);
		_table = (TableLayout)findViewById(R.id.table_code_items);
		
	}
	public void setData(List<CodeInfo> list){
		if (_table==null)
			return;
		for(int index=0;index<list.size();index++){
			CodeInfo codeinfo = list.get(index);
			TableRow row = new TableRow(_context);
			InformationRow detail = new InformationRow(_context,null);
			detail.setData(codeinfo.title,codeinfo.detail);
			row.addView(detail);
			_table.addView(row);
			if (index==0){
				detail.setBeginBackend();
			}else if (index==list.size()-1){
				detail.setEndBackend();
			}
		}
	}
	
}
