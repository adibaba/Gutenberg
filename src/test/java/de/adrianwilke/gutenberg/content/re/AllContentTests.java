package de.adrianwilke.gutenberg.content.re;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TextTest.class, SectionsTest.class, CutterTest.class, ChapterSearchTest.class })

public class AllContentTests {
}