package com.rockwellcollins.atc.agree.tests;

import com.google.inject.Inject;
import com.itemis.xtext.testing.FluentIssueCollection;
import com.itemis.xtext.testing.XtextTest;
import com.rockwellcollins.atc.agree.agree.AgreeContractLibrary;
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause;
import com.rockwellcollins.atc.agree.tests.AgreeInjectorProvider;
import com.rockwellcollins.atc.agree.tests.testsupport.TestHelper;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexLibrary;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.DefaultAnnexLibrary;
import org.osate.aadl2.DefaultAnnexSubclause;

@RunWith(XtextRunner.class)
@InjectWith(AgreeInjectorProvider.class)
@SuppressWarnings("all")
public class AgreeParsingTest extends XtextTest {
  @Inject
  private TestHelper<AadlPackage> parseHelper;
  
  @Test
  public void testParsing() {
    try {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("package example");
      _builder.newLine();
      _builder.append("public");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("annex agree {**");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("const x : int = 2;");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("**};");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("system sys");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("-- subcomponents");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("--   none;");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("-- properties");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("--   none;");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("annex agree {**");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("const x : int = 2;");
      _builder.newLine();
      _builder.append("      ");
      _builder.append("-- guarantee sys_g_1 \"Trivial\" : true;");
      _builder.newLine();
      _builder.append("    ");
      _builder.append("**};");
      _builder.newLine();
      _builder.append("  ");
      _builder.append("end sys;");
      _builder.newLine();
      _builder.append("end example;");
      _builder.newLine();
      final String model = _builder.toString();
      final FluentIssueCollection testFileResult = this.issues = this.parseHelper.testString(model);
      EObject _head = IterableExtensions.<EObject>head(testFileResult.getResource().getContents());
      final Procedure1<AadlPackage> _function = (AadlPackage it) -> {
        Assert.assertEquals(1, it.getPublicSection().getOwnedAnnexLibraries().size());
        AnnexLibrary _head_1 = IterableExtensions.<AnnexLibrary>head(it.getPublicSection().getOwnedAnnexLibraries());
        final Procedure1<DefaultAnnexLibrary> _function_1 = (DefaultAnnexLibrary it_1) -> {
          AnnexLibrary _parsedAnnexLibrary = it_1.getParsedAnnexLibrary();
          Assert.assertTrue((_parsedAnnexLibrary instanceof AgreeContractLibrary));
        };
        ObjectExtensions.<DefaultAnnexLibrary>operator_doubleArrow(
          ((DefaultAnnexLibrary) _head_1), _function_1);
        Classifier _head_2 = IterableExtensions.<Classifier>head(it.getPublicSection().getOwnedClassifiers());
        final Procedure1<Classifier> _function_2 = (Classifier it_1) -> {
          Assert.assertEquals("sys", it_1.getName());
          Assert.assertEquals(1, it_1.getOwnedAnnexSubclauses().size());
          AnnexSubclause _head_3 = IterableExtensions.<AnnexSubclause>head(it_1.getOwnedAnnexSubclauses());
          final Procedure1<DefaultAnnexSubclause> _function_3 = (DefaultAnnexSubclause it_2) -> {
            AnnexSubclause _parsedAnnexSubclause = it_2.getParsedAnnexSubclause();
            Assert.assertTrue((_parsedAnnexSubclause instanceof AgreeContractSubclause));
          };
          ObjectExtensions.<DefaultAnnexSubclause>operator_doubleArrow(
            ((DefaultAnnexSubclause) _head_3), _function_3);
        };
        ObjectExtensions.<Classifier>operator_doubleArrow(_head_2, _function_2);
      };
      ObjectExtensions.<AadlPackage>operator_doubleArrow(
        ((AadlPackage) _head), _function);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
