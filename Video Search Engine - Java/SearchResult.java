
public class SearchResult {
	public String title;
	public String body, body2;
	public String path;
	public int startT;
	public boolean isVid;
	
	public SearchResult(String _title, String _body, String _path, int _startT, boolean _isVid) {
		title = _title;
		body = _body;
		path = _path;
		startT = _startT;
		isVid = _isVid;
		
		body2 = "";
		if (body.length() > 130) {
			body2 = body.substring(130, body.length());
			body = body.substring(0, 130);
			if (body.charAt(129) != ' ' && body2.charAt(0) != ' ') { body += "-"; }
			if (body2.charAt(0) == ' ') { body2 = body2.substring(1); }
		}
		if (body2.length() > 130) {
			body2 = body2.substring(0, 130) + " ...";
		}
	}
}
