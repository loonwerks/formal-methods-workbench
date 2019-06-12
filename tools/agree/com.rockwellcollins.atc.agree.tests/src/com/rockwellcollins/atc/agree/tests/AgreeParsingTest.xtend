package com.rockwellcollins.atc.agree.tests

import com.google.inject.Inject

//import org.junit.jupiter.api.Assertions
//import org.junit.jupiter.api.Disabled
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.^extension.ExtendWith
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

import org.eclipse.xtext.testing.XtextRunner
import com.itemis.xtext.testing.XtextTest

import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.util.ParseHelper

import org.osate.aadl2.AadlPackage
import org.osate.aadl2.SystemType
import com.rockwellcollins.atc.agree.agree.AgreeLibrary
import com.rockwellcollins.atc.agree.agree.AgreeSubclause
import com.rockwellcollins.atc.agree.agree.AgreeContract
import com.rockwellcollins.atc.agree.tests.testsupport.TestHelper
import org.osate.aadl2.DefaultAnnexSubclause
import org.osate.aadl2.DefaultAnnexLibrary
import com.rockwellcollins.atc.agree.agree.AgreeContractLibrary
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause

@RunWith(XtextRunner)
@InjectWith(AgreeInjectorProvider)
class AgreeParsingTest extends XtextTest {

	@Inject
	TestHelper<AadlPackage> parseHelper

	@Test
	def void testParsing() {
		val model = '''
			package example
			public
			  annex agree {**
			    const x : int = 2;
			  **};
			  system sys
			    -- subcomponents
			    --   none;
			    -- properties
			    --   none;
			    annex agree {**
			      const x : int = 2;
			      -- guarantee sys_g_1 "Trivial" : true;
			    **};
			  end sys;
			end example;
		'''

		val testFileResult = issues = parseHelper.testString(model)

		testFileResult.resource.contents.head as AadlPackage => [
			Assert.assertEquals(1, publicSection.ownedAnnexLibraries.size)
			publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
				Assert.assertTrue(parsedAnnexLibrary instanceof AgreeContractLibrary)
			]
			publicSection.ownedClassifiers.head => [
				Assert.assertEquals("sys", name)
				Assert.assertEquals(1, ownedAnnexSubclauses.size)
				ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
					Assert.assertTrue(parsedAnnexSubclause instanceof AgreeContractSubclause)
				]
			]
		] 

//		val pack = parseHelper.parseString(model)
//		val errors = pack.eResource.errors
//		Assert::assertEquals("example", pack.name)
//
//		val sys = pack.publicSection.ownedClassifiers.get(0) as SystemType
//		Assert::assertEquals("sys", sys.name)
//
//		val agreeContracts = EcoreUtil2.getAllContentsOfType(pack, AgreeContract)
//		val agreeLibraries = EcoreUtil2.getAllContentsOfType(pack, AgreeLibrary)
//		val agreeSubclauses = EcoreUtil2.getAllContentsOfType(pack, AgreeSubclause)
//		Assert::assertEquals(1, agreeLibraries.size)
//		Assert::assertEquals(2, agreeContracts.size)
//		Assert::assertEquals(1, agreeSubclauses.size)
//		Assert.assertTrue('''Unexpected errors: «errors.join(", ")»''', errors.isEmpty)
	}

//	@Inject
//	TestHelper<AadlPackage> testHelper
//
//	@Test
//	def void testParsing() {
//		val model = '''
//			package example
//			public
//			  annex agree {**
//			    const x : int = 2;
//			  **};
//			  system sys
//			    subcomponents
//			      none;
//			    properties
//			      none;
//			    annex agree {**
//			      guarantee sys_g_1 "Trivial" : true;
//			    **};
//			end example;
//		'''
//
//		val pack = testHelper.parseString(model)
//		Assertions::assertEquals("example", pack.name)
//
//		val sys = pack.publicSection.ownedClassifiers.get(0) as SystemType
//		Assertions::assertEquals("sys", sys.name)
//	}

}