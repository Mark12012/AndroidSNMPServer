import java.util.List;

public class TableModel {
	private List<String> columns;
	private List<List<String>> rows;
	
	public TableModel(List<String> columns, List<List<String>> rows)
	{
		this.setColumns(columns);
		this.setRows(rows);
	}

	public List<String> getColumns() {
		return columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<List<String>> getRows() {
		return rows;
	}

	public void setRows(List<List<String>> rows) {
		this.rows = rows;
	}

}
