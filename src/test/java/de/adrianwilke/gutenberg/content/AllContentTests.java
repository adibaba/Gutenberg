package de.adrianwilke.gutenberg.content;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TextTest.class, CutterTest.class, CleanerTest.class, SectionsTest.class, ChapterSearchTest.class })

public class AllContentTests {
}