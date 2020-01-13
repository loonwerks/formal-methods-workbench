package com.rockwellcollins.atc.agree.tests

import com.google.inject.Inject

import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import com.itemis.xtext.testing.XtextTest

import org.osate.aadl2.AadlPackage
import org.osate.aadl2.DefaultAnnexLibrary
import org.osate.aadl2.DefaultAnnexSubclause
import org.osate.aadl2.SystemImplementation
import org.osate.aadl2.SystemType

import com.rockwellcollins.atc.agree.agree.AgreeContract
import com.rockwellcollins.atc.agree.agree.AgreeContractLibrary
import com.rockwellcollins.atc.agree.agree.AgreeContractSubclause
import com.rockwellcollins.atc.agree.agree.AssignStatement
import com.rockwellcollins.atc.agree.agree.AssumeStatement
import com.rockwellcollins.atc.agree.agree.BinaryExpr
import com.rockwellcollins.atc.agree.agree.CallExpr
import com.rockwellcollins.atc.agree.agree.ConstStatement
import com.rockwellcollins.atc.agree.agree.DoubleDotRef
import com.rockwellcollins.atc.agree.agree.EnumStatement
import com.rockwellcollins.atc.agree.agree.EqStatement
import com.rockwellcollins.atc.agree.agree.EventExpr
import com.rockwellcollins.atc.agree.agree.FnDef
import com.rockwellcollins.atc.agree.agree.GuaranteeStatement
import com.rockwellcollins.atc.agree.agree.InputStatement
import com.rockwellcollins.atc.agree.agree.LemmaStatement
import com.rockwellcollins.atc.agree.agree.LinearizationDef
import com.rockwellcollins.atc.agree.agree.NamedElmExpr
import com.rockwellcollins.atc.agree.agree.NamedID
import com.rockwellcollins.atc.agree.agree.NodeBodyExpr
import com.rockwellcollins.atc.agree.agree.NodeDef
import com.rockwellcollins.atc.agree.agree.NodeEq
import com.rockwellcollins.atc.agree.agree.PreExpr
import com.rockwellcollins.atc.agree.agree.RecordDef
import com.rockwellcollins.atc.agree.agree.RecordLitExpr
import com.rockwellcollins.atc.agree.agree.SelectionExpr

import com.rockwellcollins.atc.agree.tests.testsupport.TestHelper

@RunWith(XtextRunner)
@InjectWith(AgreeInjectorProvider)
class AgreeLinkingTest extends XtextTest {

    @Inject
    TestHelper<AadlPackage> testHelper

    @Test
    def void testArgInFunDef() {
        val model = '''
            Package TestPackage
            public
                annex agree {**
                    fun comb (x : int, y : int) : int = x + y;
                **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as FnDef => [
                            val args = args
                            expr as BinaryExpr => [
                                left as NamedElmExpr => [
                                    Assert.assertTrue(args.contains(elm))
                                ]
                                right as NamedElmExpr => [
                                    Assert.assertTrue(args.contains(elm))
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testArgInNodeDef() {
        val model = '''
            Package TestPackage
            public
                annex agree {**
                    node comb (x : int, y : int) returns (r : int);
                    var
                        l : int;
                    let
                        l = 0 -> (pre(l) + 1);
                        r = x + (y + l);
                    tel;
                **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as NodeDef => [
                            val args = args
                            val rets = rets
                            nodeBody as NodeBodyExpr => [
                                val locals = locs
                                stmts.head as NodeEq => [
                                    Assert.assertEquals(locals.head, lhs.head)
                                    Assert.assertEquals(locals.head,
                                        ((((expr as BinaryExpr).right as BinaryExpr).left as PreExpr).
                                            expr as NamedElmExpr).elm)
                                ]
                                stmts.tail.head as NodeEq => [
                                    Assert.assertEquals(rets.head, lhs.head)
                                    expr as BinaryExpr => [
                                        Assert.assertTrue(args.contains((left as NamedElmExpr).elm))
                                        right as BinaryExpr => [
                                            Assert.assertTrue(args.contains((left as NamedElmExpr).elm))
                                            Assert.assertEquals(locals.head, (right as NamedElmExpr).elm)
                                        ]
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testArgInLinearizationDef() {
        val model = '''
            Package TestPackage
            public
                annex agree {**
                    linearization comb (x : real) over [-1.0 .. 1.0] within 0.1 : x;
                **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as LinearizationDef => [
                            val args = args
                            exprBody as NamedElmExpr => [
                                Assert.assertEquals(args.head, elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testRecordDefInAnnexLibrary() {
        val model = '''
            package TestPackage
            public
              annex agree {**
                type Coord2D = struct { x : int, y : int };
                const loc : Coord2D = Coord2D { x = 1; y = 2 };
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        val recordDef = specs.head as RecordDef
                        specs.tail.head as ConstStatement => [
                            Assert.assertTrue(^type instanceof DoubleDotRef)
                            ^type as DoubleDotRef => [
                                Assert.assertEquals(recordDef, elm)
                            ]
                            Assert.assertTrue(expr instanceof RecordLitExpr)
                            expr as RecordLitExpr => [
                                Assert.assertTrue(recordType instanceof DoubleDotRef)
                                recordType as DoubleDotRef => [
                                    Assert.assertEquals(recordDef, elm)
                                ]
                                // ToDo: Do we want to bother looking at the elements?
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Ignore("known problem in AgreeScopeProvider")
    @Test
    def void testEnumerationInAnnexLibrary() {
        val model = '''
            package TestPackage
            public
              annex agree {**
                enum PixelColor = { Red, Green, Blue };
                const y : PixelColor = Red;
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        val enumStmt = specs.head as EnumStatement
                        specs.tail.head as ConstStatement => [
                            Assert.assertTrue(^type instanceof DoubleDotRef)
                            ^type as DoubleDotRef => [
                                Assert.assertEquals(enumStmt, elm)
                            ]
                            Assert.assertTrue(expr instanceof NamedElmExpr)
                            expr as NamedElmExpr => [
                                Assert.assertTrue(elm instanceof NamedID)
                                elm as NamedID => [
                                    Assert.assertEquals("Red", name)
                                    val containingEnumStatement = EcoreUtil2.getContainerOfType(it, EnumStatement)
                                    Assert.assertEquals(enumStmt, containingEnumStatement)
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testConstantInAnnexLibrary() {
        val model = '''
            package TestPackage
            public
              annex agree {**
                const x : int = 2;
                const y : int = x;
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        val constStmt = specs.head as ConstStatement
                        specs.tail.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof NamedElmExpr)
                            expr as NamedElmExpr => [
                                Assert.assertEquals(constStmt, elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testFnDefInAnnexLibrary() {
        val model = '''
            package TestPackage
            public
              annex agree {**
                fun afun (x : int) : int = x + 1;
                const y : int = afun(0);
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        val fnDefStmt = specs.head as FnDef
                        specs.tail.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof CallExpr)
                            expr as CallExpr => [
                                Assert.assertEquals(fnDefStmt, ref.elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testNodeDefInAnnexLibrary() {
        val model = '''
            package TestPackage
            public
              annex agree {**
                node anode (x : int) returns (r : int);
                let
                  r = x + 1;
                tel;
                const y : int = anode(0);
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        val nodeDefStmt = specs.head as NodeDef
                        specs.tail.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof CallExpr)
                            expr as CallExpr => [
                                Assert.assertEquals(nodeDefStmt, ref.elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testLinearizationDefInAnnexLibrary() {
        val model = '''
            package TestPackage
            public
              with Linearizer;
              annex agree {**
                linearization lsin (x : real) over [-1.0 .. 1.0] within 0.2 : Linearizer::sin(x);
                const y : real = lsin(0.0);
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        val linearizationDefStmt = specs.head as LinearizationDef
                        specs.tail.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof CallExpr)
                            expr as CallExpr => [
                                Assert.assertEquals(linearizationDefStmt, ref.elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testRecordDefInReferencedAnnexLibrary() {
        val lib = '''
            package LibPackage
            public
              annex agree {**
                type Coord2D = struct { x : int, y : int };
              **};
            end LibPackage;
        '''
        val model = '''
            package TestPackage
            public
              with LibPackage;
              annex agree {**
                const loc : LibPackage::Coord2D = LibPackage::Coord2D { x = 1; y = 2 };
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model, lib)
        testFileResult.resource.contents.head as AadlPackage => [
            Assert.assertEquals("TestPackage", name)
            Assert.assertEquals("LibPackage", (publicSection.importedUnits.head as AadlPackage).name)
            val recordDef = ((((publicSection.importedUnits.head as AadlPackage).publicSection.ownedAnnexLibraries.
                head as DefaultAnnexLibrary).parsedAnnexLibrary as AgreeContractLibrary).contract as AgreeContract).
                specs.head as RecordDef
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as ConstStatement => [
                            Assert.assertTrue(^type instanceof DoubleDotRef)
                            ^type as DoubleDotRef => [
                                Assert.assertEquals(recordDef, elm)
                            ]
                            Assert.assertTrue(expr instanceof RecordLitExpr)
                            expr as RecordLitExpr => [
                                Assert.assertTrue(recordType instanceof DoubleDotRef)
                                recordType as DoubleDotRef => [
                                    Assert.assertEquals(recordDef, elm)
                                ]
                                // ToDo: Do we want to bother looking at the elements?
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testEnumerationInReferencedAnnexLibrary() {
        val lib = '''
            package LibPackage
            public
              annex agree {**
                enum PixelColor = { Red, Green, Blue };
              **};
            end LibPackage;
        '''
        val model = '''
            package TestPackage
            public
              with LibPackage;
              annex agree {**
                const y : LibPackage::PixelColor = LibPackage::Red;
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model, lib)
        testFileResult.resource.contents.head as AadlPackage => [
            Assert.assertEquals("TestPackage", name)
            Assert.assertEquals("LibPackage", (publicSection.importedUnits.head as AadlPackage).name)
            val enumStmt = ((((publicSection.importedUnits.head as AadlPackage).publicSection.ownedAnnexLibraries.
                head as DefaultAnnexLibrary).parsedAnnexLibrary as AgreeContractLibrary).contract as AgreeContract).
                specs.head as EnumStatement
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as ConstStatement => [
                            Assert.assertTrue(^type instanceof DoubleDotRef)
                            ^type as DoubleDotRef => [
                                Assert.assertEquals(enumStmt, elm)
                            ]
                            Assert.assertTrue(expr instanceof NamedElmExpr)
                            expr as NamedElmExpr => [
                                Assert.assertTrue(elm instanceof NamedID)
                                elm as NamedID => [
                                    Assert.assertEquals("Red", name)
                                    val containingEnumStatement = EcoreUtil2.getContainerOfType(it, EnumStatement)
                                    Assert.assertEquals(enumStmt, containingEnumStatement)
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testConstantInReferencedAnnexLibrary() {
        val lib = '''
            package LibPackage
            public
              annex agree {**
                const x : int = 2;
              **};
            end LibPackage;
        '''
        val model = '''
            package TestPackage
            public
              with LibPackage;
              annex agree {**
                const y : int = LibPackage::x;
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model, lib)
        testFileResult.resource.contents.head as AadlPackage => [
            Assert.assertEquals("TestPackage", name)
            Assert.assertEquals("LibPackage", (publicSection.importedUnits.head as AadlPackage).name)
            val constStmt = ((((publicSection.importedUnits.head as AadlPackage).publicSection.ownedAnnexLibraries.
                head as DefaultAnnexLibrary).parsedAnnexLibrary as AgreeContractLibrary).contract as AgreeContract).
                specs.head as ConstStatement
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof NamedElmExpr)
                            expr as NamedElmExpr => [
                                Assert.assertTrue(elm instanceof ConstStatement)
                                Assert.assertEquals(constStmt, elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testFnDefInReferencedAnnexLibrary() {
        val lib = '''
            package LibPackage
            public
              annex agree {**
                fun afun (x : int) : int = x + 1;
              **};
            end LibPackage;
        '''
        val model = '''
            package TestPackage
            public
              with LibPackage;
              annex agree {**
                const y : int = LibPackage::afun(0);
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model, lib)
        testFileResult.resource.contents.head as AadlPackage => [
            Assert.assertEquals("TestPackage", name)
            Assert.assertEquals("LibPackage", (publicSection.importedUnits.head as AadlPackage).name)
            val fnDef = ((((publicSection.importedUnits.head as AadlPackage).publicSection.ownedAnnexLibraries.
                head as DefaultAnnexLibrary).parsedAnnexLibrary as AgreeContractLibrary).contract as AgreeContract).
                specs.head as FnDef
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof CallExpr)
                            expr as CallExpr => [
                                Assert.assertTrue(ref.elm instanceof FnDef)
                                Assert.assertEquals(fnDef, ref.elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testNodeDefInReferencedAnnexLibrary() {
        val lib = '''
            package LibPackage
            public
              annex agree {**
                node anode (x : int) returns (r : int);
                let
                  r = x + 1;
                tel;
              **};
            end LibPackage;
        '''
        val model = '''
            package TestPackage
            public
              with LibPackage;
              annex agree {**
                const y : int = LibPackage::anode(0);
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model, lib)
        testFileResult.resource.contents.head as AadlPackage => [
            Assert.assertEquals("TestPackage", name)
            Assert.assertEquals("LibPackage", (publicSection.importedUnits.head as AadlPackage).name)
            val nodeDef = ((((publicSection.importedUnits.head as AadlPackage).publicSection.ownedAnnexLibraries.
                head as DefaultAnnexLibrary).parsedAnnexLibrary as AgreeContractLibrary).contract as AgreeContract).
                specs.head as NodeDef
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof CallExpr)
                            expr as CallExpr => [
                                Assert.assertTrue(ref.elm instanceof NodeDef)
                                Assert.assertEquals(nodeDef, ref.elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testLinearizationDefInReferencedAnnexLibrary() {
        val lib = '''
            package LibPackage
            public
              with Linearizer;
              annex agree {**
                linearization lsin (x : real) over [-1.0 .. 1.0] within 0.2 : Linearizer::sin(x);
              **};
            end LibPackage;
        '''
        val model = '''
            package TestPackage
            public
              with LibPackage;
              annex agree {**
                const y : real = LibPackage::lsin(0.0);
              **};
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model, lib)
        testFileResult.resource.contents.head as AadlPackage => [
            Assert.assertEquals("TestPackage", name)
            Assert.assertEquals("LibPackage", (publicSection.importedUnits.head as AadlPackage).name)
            val nodeDef = ((((publicSection.importedUnits.head as AadlPackage).publicSection.ownedAnnexLibraries.
                head as DefaultAnnexLibrary).parsedAnnexLibrary as AgreeContractLibrary).contract as AgreeContract).
                specs.head as LinearizationDef
            publicSection.ownedAnnexLibraries.head as DefaultAnnexLibrary => [
                parsedAnnexLibrary as AgreeContractLibrary => [
                    contract as AgreeContract => [
                        specs.head as ConstStatement => [
                            Assert.assertTrue(expr instanceof CallExpr)
                            expr as CallExpr => [
                                Assert.assertTrue(ref.elm instanceof LinearizationDef)
                                Assert.assertEquals(nodeDef, ref.elm)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testAgreeInputInType() {
        val model = '''
            package TestPackage
            public
              system TestSystem
                annex agree {**
                  agree_input ain : int;
                  assume "Input Assumption" : ain > 0;
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            Assert.assertTrue(specs.head instanceof InputStatement)
                            val inputStmt = specs.head as InputStatement
                            specs.tail.head as AssumeStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(inputStmt.lhs.head, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testInputDataPortInType() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  inp : in data port Base_Types::Integer;
                annex agree {**
                  assume "Input Assumption" : inp > 0;
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                val inputDataPort = ownedDataPorts.head
                Assert.assertEquals("TestPackage::TestSystem.inp", inputDataPort.getQualifiedName)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as AssumeStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(inputDataPort, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testOutputDataPortInType() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  outp : out data port Base_Types::Integer;
                annex agree {**
                  guarantee "Output Guarantee" : outp > 0;
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                val outputDataPort = ownedDataPorts.head
                Assert.assertEquals("TestPackage::TestSystem.outp", outputDataPort.getQualifiedName)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as GuaranteeStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(outputDataPort, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testInputEventPortInType() {
        val model = '''
            package TestPackage
            public
              system TestSystem
                features
                  inp : in event port;
                annex agree {**
                  assume "Input Assumption" : event(inp);
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                val inputEventPort = ownedEventPorts.head
                Assert.assertEquals("TestPackage::TestSystem.inp", inputEventPort.getQualifiedName)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as AssumeStatement => [
                                expr as EventExpr => [
                                    port as NamedElmExpr => [
                                        Assert.assertEquals(inputEventPort, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testOutputEventPortInType() {
        val model = '''
            package TestPackage
            public
              system TestSystem
                features
                  outp : out event port;
                annex agree {**
                  guarantee "Output Guarantee" : event(outp);
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                val outputEventPort = ownedEventPorts.head
                Assert.assertEquals("TestPackage::TestSystem.outp", outputEventPort.getQualifiedName)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as GuaranteeStatement => [
                                expr as EventExpr => [
                                    port as NamedElmExpr => [
                                        Assert.assertEquals(outputEventPort, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testInputEventDataPortInType() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  inp : in event data port Base_Types::Integer;
                annex agree {**
                  assume "Input Assumption" : inp > 0;
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                val inputEventDataPort = ownedEventDataPorts.head
                Assert.assertEquals("TestPackage::TestSystem.inp", inputEventDataPort.getQualifiedName)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as AssumeStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(inputEventDataPort, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testOutputEventDataPortInType() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  outp : out event data port Base_Types::Integer;
                annex agree {**
                  guarantee "Output Guarantee" : outp > 0;
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                val outputEventDataPort = ownedEventDataPorts.head
                Assert.assertEquals("TestPackage::TestSystem.outp", outputEventDataPort.getQualifiedName)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as GuaranteeStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(outputEventDataPort, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testAgreeInputInImplementation() {
        val model = '''
            package TestPackage
            public
              system TestSystem
                annex agree {**
                  agree_input ain : int;
                **}; 
              end TestSystem;
              system implementation TestSystem.impl
                annex agree {**
                  lemma "Input Lemma" : ain > 0;
                **};
              end TestSystem.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            val inputStmt = ((((publicSection.ownedClassifiers.head as SystemType).ownedAnnexSubclauses.
                head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                contract as AgreeContract).specs.head as InputStatement
            publicSection.ownedClassifiers.tail.head as SystemImplementation => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(inputStmt.lhs.head, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testInputDataPortInImplementation() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  inp : in data port Base_Types::Integer;
                annex agree {**
                  assume "Input Assumption" : inp > 0;
                **}; 
              end TestSystem;
              system implementation TestSystem.impl
                annex agree {**
                  eq internal_inp : Base_Types::Integer = inp;
                **};
              end TestSystem.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            val inputDataPort = (publicSection.ownedClassifiers.head as SystemType).ownedDataPorts.head
            Assert.assertEquals("TestPackage::TestSystem.inp", inputDataPort.getQualifiedName)
            publicSection.ownedClassifiers.tail.head as SystemImplementation => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as EqStatement => [
                                expr as NamedElmExpr => [
                                    Assert.assertEquals(inputDataPort, elm)
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testOutputDataPortInImplementation() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  outp : out data port Base_Types::Integer;
                annex agree {**
                  guarantee "Output Guarantee" : outp > 0;
                **}; 
              end TestSystem;
              system implementation TestSystem.impl
                annex agree {**
                  eq internal_outp : Base_Types::Integer = outp;
                **};
              end TestSystem.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            val inputDataPort = (publicSection.ownedClassifiers.head as SystemType).ownedDataPorts.head
            Assert.assertEquals("TestPackage::TestSystem.outp", inputDataPort.getQualifiedName)
            publicSection.ownedClassifiers.tail.head as SystemImplementation => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as EqStatement => [
                                expr as NamedElmExpr => [
                                    Assert.assertEquals(inputDataPort, elm)
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testOutputDataPortInAssignment() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  outp : out data port Base_Types::Integer;
                annex agree {**
                  guarantee "Output Guarantee" : outp > 0;
                **}; 
              end TestSystem;
              system implementation TestSystem.impl
                annex agree {**
                  assign outp = 1;
                **};
              end TestSystem.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            val inputDataPort = (publicSection.ownedClassifiers.head as SystemType).ownedDataPorts.head
            Assert.assertEquals("TestPackage::TestSystem.outp", inputDataPort.getQualifiedName)
            publicSection.ownedClassifiers.tail.head as SystemImplementation => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as AssignStatement => [
                                Assert.assertEquals(inputDataPort, id)
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testEqStatementInType() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  outp : out data port Base_Types::Integer;
                annex agree {**
                  eq x : Base_Types::Integer;
                  guarantee "Output Guarantee" : outp > x;
                **}; 
              end TestSystem;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.head as SystemType => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            val eqArg = (specs.head as EqStatement).lhs.head
                            Assert.assertEquals("x", eqArg.getQualifiedName)
                            specs.tail.head as GuaranteeStatement => [
                                expr as BinaryExpr => [
                                    right as NamedElmExpr => [
                                        Assert.assertEquals(eqArg, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testEqStatementInTypeFromImplementationReference() {
        val model = '''
            package TestPackage
            public
              with Base_Types;
              system TestSystem
                features
                  outp : out data port Base_Types::Integer;
                annex agree {**
                  eq x : Base_Types::Integer;
                  guarantee "Output Guarantee" : outp > x;
                **}; 
              end TestSystem;
              system implementation TestSystem.impl
                annex agree {**
                  lemma "Implementation Lemma" : x > 0;
                **};
              end TestSystem.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            val eqArg = (((((publicSection.ownedClassifiers.head as SystemType).ownedAnnexSubclauses.
                head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                contract as AgreeContract).specs.head as EqStatement).lhs.head
            Assert.assertEquals("x", eqArg.getQualifiedName)
            publicSection.ownedClassifiers.tail.head as SystemImplementation => [
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as NamedElmExpr => [
                                        Assert.assertEquals(eqArg, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testInputPortInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.inp > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val inputPort = (subcomponentSystem.componentType as SystemType).ownedDataPorts.head
                Assert.assertEquals("inp", inputPort.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(inputPort, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testOutputPortInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.outp > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val outputPort = (subcomponentSystem.componentType as SystemType).ownedDataPorts.tail.head
                Assert.assertEquals("outp", outputPort.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(outputPort, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Ignore("known problem in AgreeScopeProvider")
    @Test
    def void testRecordDefInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        type Coord3D = struct { x : int, y : int, z : int };
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        eq localCoord : Inner::Coord3D = Inner::Coord3D { x = 0; y = 0; z = 0 };
                        lemma "Outer Inner subcomponent lemma" : inner_sub.inp > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val localCoordDef = ((((subcomponentSystem.componentType as SystemType).ownedAnnexSubclauses.
                    head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as RecordDef
                Assert.assertEquals("Coord3D", localCoordDef.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as EqStatement => [
                                lhs.head.^type as DoubleDotRef => [
                                    Assert.assertEquals(localCoordDef, elm)
                                ] 
                                expr as RecordLitExpr => [
                                    recordType as DoubleDotRef => [
                                        Assert.assertEquals(localCoordDef, elm)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testConstStatementInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        const iconst : int = 10;
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.iconst > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val innerConst = ((((subcomponentSystem.componentType as SystemType).ownedAnnexSubclauses.
                    head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as ConstStatement
                Assert.assertEquals("iconst", innerConst.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(innerConst, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testConstStatementInNestedImplementation() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system implementation Inner.impl
                    annex agree {**
                        const iconst : int = 10;
                    **};
                end Inner.impl;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner.impl;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.iconst > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val innerConst = ((((subcomponentSystem.componentImplementation as SystemImplementation).
                    ownedAnnexSubclauses.head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as ConstStatement
                Assert.assertEquals("iconst", innerConst.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(innerConst, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testInputStatementInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        agree_input iinput : int;
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.iinput > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val innerInput = ((((subcomponentSystem.componentType as SystemType).ownedAnnexSubclauses.
                    head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as InputStatement
                Assert.assertEquals("iinput", innerInput.lhs.head.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(innerInput.lhs.head, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testEqStatementInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        eq ivar : int = 10;
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.ivar > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val innerVar = ((((subcomponentSystem.componentType as SystemType).ownedAnnexSubclauses.
                    head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as EqStatement
                Assert.assertEquals("ivar", innerVar.lhs.head.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(innerVar.lhs.head, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testEqStatementInNestedImplementation() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system implementation Inner.impl
                    annex agree {**
                        eq ivar : int = 10;
                    **};
                end Inner.impl;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner.impl;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.ivar > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val innerVar = ((((subcomponentSystem.componentImplementation as SystemImplementation).
                    ownedAnnexSubclauses.head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as EqStatement
                Assert.assertEquals("ivar", innerVar.lhs.head.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(innerVar.lhs.head, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

    @Test
    def void testAgreeInputInNestedType() {
        val model = '''
            package TestPackage
            public
                with Base_Types;
                system Inner
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        agree_input ivar : int;
                        guarantee "Inner Output Guarantee" : outp > 0;
                    **};
                end Inner;
                system Outer
                    features
                        inp : in data port Base_Types::Integer;
                        outp : out data port Base_Types::Integer;
                    annex agree {**
                        guarantee "Outer Output Guarantee" : outp > 0;
                    **};
                end Outer;
                system implementation Outer.impl
                    subcomponents
                        inner_sub : system Inner;
                    connections
                        i1 : port inp -> inner_sub.inp;
                        o1 : port inner_sub.outp -> outp;
                    annex agree {**
                        lemma "Outer Inner subcomponent lemma" : inner_sub.ivar > 0;
                    **};
                end Outer.impl;
            end TestPackage;
        '''

        val testFileResult = issues = testHelper.testString(model)
        testFileResult.resource.contents.head as AadlPackage => [
            publicSection.ownedClassifiers.tail.tail.head as SystemImplementation => [
                Assert.assertEquals("Outer.impl", name)
                val subcomponentSystem = ownedSubcomponents.head
                val innerVar = ((((subcomponentSystem.componentType as SystemType).ownedAnnexSubclauses.
                    head as DefaultAnnexSubclause).parsedAnnexSubclause as AgreeContractSubclause).
                    contract as AgreeContract).specs.head as InputStatement
                Assert.assertEquals("ivar", innerVar.lhs.head.name)
                ownedAnnexSubclauses.head as DefaultAnnexSubclause => [
                    parsedAnnexSubclause as AgreeContractSubclause => [
                        contract as AgreeContract => [
                            specs.head as LemmaStatement => [
                                expr as BinaryExpr => [
                                    left as SelectionExpr => [
                                        Assert.assertEquals(subcomponentSystem, (target as NamedElmExpr).elm)
                                        Assert.assertEquals(innerVar.lhs.head, field)
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
    }

}