/*
 Project: AVpourri
 File: MSIAVI_Container.java (com.alexkersten.avpourri.media.extractors.msiavi)
 Author: Alex Kersten
 */
package com.alexkersten.avpourri.media.extractors.msiavi;

import com.alexkersten.avpourri.media.MediaContainer;

/**
 * Why is this file named MSIAVI container, why not just make a general AVI
 * extractor?
 *
 * Let me preface this with: the AVI file format is _very bad_, and I'm not sure
 * why _anyone_ would target it when developing software. Why not use a better
 * container format like MKV?
 *
 * A few of the things that are wrong with AVI:
 *
 * 1) The info header containing content offsets, which are _real useful_ for a
 * non-linear video editing program is at the _end_ of the file. Not, like, at
 * the beginning, where you'd expect a header to be. What in the world?
 *
 * 2) This header is actually completely optional - you might be stuck having to
 * scan through the entire file and find content streams yourself!
 *
 * 3) If the header _is_ present, there's multiple versions! One called
 * "AVIOLDINDEX" and one called "AVIMETAINDEX" in which they've apparently tried
 * to outdo themselves and create a header for... separate headers within the
 * data stream? I'll admit I didn't spend too much time looking at this because
 * thankfully the files we're working with use the "old" header...
 *
 * 4) Within the (old) header, there's a field named `dwOffset` - exactly the
 * kind of thing we're looking for, an offset into the stream to find exactly
 * where content is. But it's not that simple! A quote from the MSDN
 * documentation: "dwOffset: Specifies the location of the data chunk in the
 * file. The value should be specified as an offset, in bytes, from the start of
 * the 'movi' list; however, in some AVI files it is given as an offset from the
 * start of the file." Great! No discernable way to tell which format any given
 * AVI uses, either!
 *
 * I could go on, but I won't. But I could. Anyway, that's why we're
 * specifically targeting AVI files produced by MSI Afterburner, and any others
 * that happen to work are just extra points. I'd prefer to spend as little time
 * as possible working with the AVI format.
 *
 * @author Alex Kersten
 */
public class MSIAVI_Container extends MediaContainer {
}
