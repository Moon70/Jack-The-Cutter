package lunartools.audiocutter.service.autocut;

public class Cutpoint {
	private int position;
	private int level;

	public Cutpoint(int position) {
		this.position=position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
