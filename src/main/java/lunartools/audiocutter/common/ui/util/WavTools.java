package lunartools.audiocutter.common.ui.util;

import lunartools.ByteTools;

public class WavTools {
	public static final int OFFSET_RIFF_MARKER=			 0;
	public static final int OFFSET_FILESIZE_MINUS_EIGHT= 4;
	public static final int OFFSET_FILE_TYPE=			 8;
	public static final int OFFSET_FORMAT_MARKER=		12;
	public static final int OFFSET_FORMAT_LENGTH=		16;
	public static final int OFFSET_FORMAT_TYPE=			20;
	public static final int OFFSET_NUMBER_OF_CHANNELS=	22;
	public static final int OFFSET_SAMPLE_RATE=			24;
	public static final int OFFSET_BYTE_RATE=			28;
	public static final int OFFSET_BLOCK_ALIGN=			32;
	public static final int OFFSET_BITS_PER_SAMPLE=		34;
	public static final int OFFSET_DATA_MARKER=			36;
	public static final int OFFSET_DATA_LENGTH=			40;

	public static byte[] createWavHeader(long wavDataLength) {
		byte[] header=new byte[44];
		header[OFFSET_RIFF_MARKER+0]='R';
		header[OFFSET_RIFF_MARKER+1]='I';
		header[OFFSET_RIFF_MARKER+2]='F';
		header[OFFSET_RIFF_MARKER+3]='F';

		header[OFFSET_FILE_TYPE+0]='W';
		header[OFFSET_FILE_TYPE+1]='A';
		header[OFFSET_FILE_TYPE+2]='V';
		header[OFFSET_FILE_TYPE+3]='E';

		header[OFFSET_FORMAT_MARKER+0]='f';
		header[OFFSET_FORMAT_MARKER+1]='m';
		header[OFFSET_FORMAT_MARKER+2]='t';
		header[OFFSET_FORMAT_MARKER+3]=' ';

		ByteTools.putLongWordAtPositionLittleEndian(16, header, OFFSET_FORMAT_LENGTH);

		ByteTools.putWordAtPositionLittleEndian(1, header, OFFSET_FORMAT_TYPE);

		ByteTools.putWordAtPositionLittleEndian(2, header, OFFSET_NUMBER_OF_CHANNELS);

		ByteTools.putLongWordAtPositionLittleEndian(44100, header, OFFSET_SAMPLE_RATE);

		ByteTools.putLongWordAtPositionLittleEndian(176400, header, OFFSET_BYTE_RATE);

		ByteTools.putWordAtPositionLittleEndian(4, header, OFFSET_BLOCK_ALIGN);

		ByteTools.putWordAtPositionLittleEndian(16, header, OFFSET_BITS_PER_SAMPLE);

		header[OFFSET_DATA_MARKER+0]='d';
		header[OFFSET_DATA_MARKER+1]='a';
		header[OFFSET_DATA_MARKER+2]='t';
		header[OFFSET_DATA_MARKER+3]='a';

		ByteTools.putLongWordAtPositionLittleEndian(wavDataLength+44-8,header, OFFSET_FILESIZE_MINUS_EIGHT);
		ByteTools.putLongWordAtPositionLittleEndian(wavDataLength, header, OFFSET_DATA_LENGTH);
		return header;
	}

}
