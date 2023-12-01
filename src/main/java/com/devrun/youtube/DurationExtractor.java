
package com.devrun.youtube;

import java.io.File;
import java.io.IOException;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;

public class DurationExtractor {

	public double extract(File source) throws IOException, JCodecException {
			FileChannelWrapper channel = NIOUtils.readableChannel(source);
			FrameGrab frameGrab = FrameGrab.createFrameGrab(channel);
			double durationInSeconds = frameGrab.getVideoTrack().getMeta().getTotalDuration();
			channel.close();
			return durationInSeconds;
	}
}
