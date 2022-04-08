package lunartools.audiocutter;

public class AudioSection {
	private String name;
	private int position;

	public AudioSection(int position) {
		this.position=position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String toString() {
		return this.getClass().getSimpleName()+": name="+name+", position="+position;
	}
}
