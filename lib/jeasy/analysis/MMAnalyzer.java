// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Unknown Source

package jeasy.analysis;

import java.io.Reader;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

// Referenced classes of package jeasy.analysis:
//			lIlllIIIIIllIlII, IIlIIlIIlIllIlIl, lIIllIlIlIIIllll

public class MMAnalyzer extends lIlllIIIIIllIlII
{

	private final int _$1;

	public MMAnalyzer()
	{
		this(0);
	}

	public MMAnalyzer(int i)
	{
		_$1 = i;
	}

	public TokenStream tokenStream(String s, Reader reader)
	{
		JVM INSTR new #3   <Class lIIllIlIlIIIllll>;
		JVM INSTR dup ;
		JVM INSTR swap ;
		reader;
		_$1;
		lIIllIlIlIIIllll();
		lIIllIlIlIIIllll liillililiiillll;
		liillililiiillll;
		JVM INSTR new #1   <Class IIlIIlIIlIllIlIl>;
		JVM INSTR dup ;
		JVM INSTR swap ;
		liillililiiillll;
		lIlllIIIIIllIlII._$2();
		lIlllIIIIIllIlII._$5();
		IIlIIlIIlIllIlIl();
		IIlIIlIIlIllIlIl iiliiliilillilil;
		iiliiliilillilil;
		JVM INSTR new #5   <Class StopFilter>;
		JVM INSTR dup ;
		JVM INSTR swap ;
		iiliiliilillilil;
		lIlllIIIIIllIlII._$5();
		true;
		StopFilter();
		return;
	}
}
