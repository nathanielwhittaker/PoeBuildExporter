package poebuildexporter.code.poeapi;

public class PathOfExileTradeApiResponse {

	private String id;
	private String[] result;
	private int total;

	public PathOfExileTradeApiResponse() {

	}

	public PathOfExileTradeApiResponse(String id, String[] result, int total) {
		this.id = id;
		this.result = result;
		this.total = total;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setResult(String[] result) {
		this.result = result;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getId() {
		return id;
	}

	public String[] getResult() {
		return result;
	}

	public int getTotal() {
		return total;
	}
}
