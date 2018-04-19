package org.eclipse.epsilon.evl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.apache.commons.csv.CSVFormat;
import org.eclipse.epsilon.base.incremental.dom.TracedExecutableBlock;
import org.eclipse.epsilon.base.incremental.trace.IAccess;
import org.eclipse.epsilon.base.incremental.trace.IElementAccess;
import org.eclipse.epsilon.base.incremental.trace.IModuleElementTrace;
import org.eclipse.epsilon.base.incremental.trace.IPropertyAccess;
import org.eclipse.epsilon.base.incremental.trace.impl.ElementAccess;
import org.eclipse.epsilon.common.module.ModuleElement;
import org.eclipse.epsilon.common.parse.AST;
import org.eclipse.epsilon.common.parse.EpsilonTreeAdaptor;
import org.eclipse.epsilon.common.util.AstUtil;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.emc.csv.CsvModel;
import org.eclipse.epsilon.emc.csv.incremental.CsvModelIncremental;
import org.eclipse.epsilon.emc.csv.test.util.CsvAddRowInjector;
import org.eclipse.epsilon.emc.csv.test.util.CsvAppendMethod;
import org.eclipse.epsilon.emc.csv.test.util.CsvChangeInjector;
import org.eclipse.epsilon.eol.dom.ExecutableBlock;
import org.eclipse.epsilon.eol.exceptions.EolRuntimeException;
import org.eclipse.epsilon.eol.execute.context.IEolContext;
import org.eclipse.epsilon.eol.execute.control.IExecutionListener;
import org.eclipse.epsilon.eol.models.IRelativePathResolver;
import org.eclipse.epsilon.evl.dom.Fix;
import org.eclipse.epsilon.evl.engine.incremental.test.util.InMemoryEvlTraceManager;
import org.eclipse.epsilon.evl.incremental.dom.TracedConstraint;
import org.eclipse.epsilon.evl.incremental.dom.TracedConstraintContext;
import org.eclipse.epsilon.evl.incremental.dom.TracedGuardBlock;
import org.eclipse.epsilon.evl.incremental.execute.context.TracedEvlContext;
import org.eclipse.epsilon.evl.incremental.trace.IContextTrace;
import org.eclipse.epsilon.evl.incremental.trace.IInvariantTrace;
import org.eclipse.epsilon.evl.incremental.trace.IMessageTrace;
import org.eclipse.epsilon.evl.parse.EvlParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author Horacio Hoyos Rodriguez
 *
 */
@RunWith(Suite.class)
@SuiteClasses({IncrementalEvlModuleIntegrationTests.PreexecutionTests.class,
			   IncrementalEvlModuleIntegrationTests.ExecutionTests.class,
	           IncrementalEvlModuleIntegrationTests.PostexecutionTests.class})
public class IncrementalEvlModuleIntegrationTests {
	
	/**
	 * Test that the correct EVL model elements are created in the trace model
	 * @author Horacio Hoyos Rodriguez
	 *
	 */
	public static class PreexecutionTests {
		
		private IncrementalEvlModule module;
		private AST moduleAST;
		private File evlFile;

		@Before
		public void setup() throws URISyntaxException {
			module = new IncrementalEvlModule();
			evlFile = new File(IncrementalEvlModuleIntegrationTests.class.getResource("testPreExecution.evl").toURI());
		}
		
		@Test
		public void testAdaptCst() throws Exception {
			URI evlUri = evlFile.toURI();
			InputStream noTabsStream = evlUri.toURL().openStream();
			final Lexer lexer = module.createLexer(new ANTLRInputStream(noTabsStream ));
		    final CommonTokenStream stream = new CommonTokenStream(lexer);
			final EpsilonTreeAdaptor adaptor = new EpsilonTreeAdaptor(evlUri, module);

			EvlParser parser = new EvlParser(stream);
			parser.setDeepTreeAdaptor(adaptor);
			moduleAST = (AST) parser.evlModule().getTree();
			module.adapt(moduleAST, null);
			// One ConstraintContext
			List<AST> contexts = AstUtil.getChildren(moduleAST, EvlParser.CONTEXT);
			assertThat(contexts.size(), is(1));
			AST singleContextAST = contexts.get(0);
			ModuleElement constraintContextME = module.adapt(singleContextAST, module);
			assertThat(constraintContextME, instanceOf(TracedConstraintContext.class));
			List<AST> invariants = AstUtil.getChildren(singleContextAST, EvlParser.CONSTRAINT);
			assertThat(invariants.size(), is(4));
			
			// UnGuardedSimple
			AST unGuardedSimpleAST = invariants.get(0);
			ModuleElement unGuardedSimpleME = module.adapt(unGuardedSimpleAST, constraintContextME);
			assertThat(unGuardedSimpleME, instanceOf(TracedConstraint.class));
			unGuardedSimpleME.setParent(constraintContextME);
			constraintContextME.getChildren().add(unGuardedSimpleME);
			ModuleElement checkBlock0ME = module.adapt(AstUtil.getChild(unGuardedSimpleAST, EvlParser.CHECK), unGuardedSimpleME);
			assertThat(checkBlock0ME, instanceOf(TracedExecutableBlock.class));
			checkBlock0ME.setParent(unGuardedSimpleME);
			unGuardedSimpleME.getChildren().add(checkBlock0ME);
			
			// GuardedSimple
			AST guardedSimpleAST = invariants.get(1);
			ModuleElement guardedSimpleME = module.adapt(guardedSimpleAST, constraintContextME);
			assertThat(guardedSimpleME, instanceOf(TracedConstraint.class));
			guardedSimpleME.setParent(constraintContextME);
			constraintContextME.getChildren().add(guardedSimpleME);
			ModuleElement guardBlock1Me = module.adapt(AstUtil.getChild(guardedSimpleAST, EvlParser.GUARD), guardedSimpleME);
			assertThat(guardBlock1Me, instanceOf(TracedGuardBlock.class));
			guardBlock1Me.setParent(guardedSimpleME);
			guardedSimpleME.getChildren().add(guardBlock1Me);
			ModuleElement checkBlock1ME = module.adapt(AstUtil.getChild(guardedSimpleAST, EvlParser.CHECK), guardedSimpleME);
			assertThat(checkBlock1ME, instanceOf(TracedExecutableBlock.class));
			checkBlock1ME.setParent(guardedSimpleME);
			guardedSimpleME.getChildren().add(checkBlock1ME);
			
			// GuardedWithMessage
			AST guardedWithMessageAST = invariants.get(2);
			ModuleElement guardedWithMessageME = module.adapt(guardedWithMessageAST, constraintContextME);
			assertThat(guardedWithMessageME, instanceOf(TracedConstraint.class));
			guardedWithMessageME.setParent(constraintContextME);
			constraintContextME.getChildren().add(guardedWithMessageME);
			ModuleElement guardBlock2Me = module.adapt(AstUtil.getChild(guardedWithMessageAST, EvlParser.GUARD), guardedWithMessageME);
			assertThat(guardBlock2Me, instanceOf(TracedGuardBlock.class));
			guardBlock2Me.setParent(guardedWithMessageME);
			guardedWithMessageME.getChildren().add(guardBlock2Me);
			ModuleElement checkBlock2ME = module.adapt(AstUtil.getChild(guardedWithMessageAST, EvlParser.CHECK), guardedWithMessageME);
			assertThat(checkBlock2ME, instanceOf(TracedExecutableBlock.class));
			checkBlock2ME.setParent(guardedWithMessageME);
			guardedWithMessageME.getChildren().add(checkBlock2ME);
			ModuleElement messageBlock2Me = module.adapt(AstUtil.getChild(guardedWithMessageAST, EvlParser.MESSAGE), guardedWithMessageME);
			assertThat(messageBlock2Me, instanceOf(TracedExecutableBlock.class));
			messageBlock2Me.setParent(guardedWithMessageME);
			guardedWithMessageME.getChildren().add(messageBlock2Me);
			
			// UnguardedWithFix
			AST unguardedWithFixAST = invariants.get(3);
			ModuleElement unguardedWithFixME = module.adapt(unguardedWithFixAST, constraintContextME);
			assertThat(unguardedWithFixME, instanceOf(TracedConstraint.class));
			unguardedWithFixME.setParent(constraintContextME);
			constraintContextME.getChildren().add(unguardedWithFixME);
			ModuleElement checkBlock3ME = module.adapt(AstUtil.getChild(unguardedWithFixAST, EvlParser.CHECK), unguardedWithFixME);
			assertThat(checkBlock3ME, instanceOf(TracedExecutableBlock.class));
			checkBlock3ME.setParent(unguardedWithFixME);
			unguardedWithFixME.getChildren().add(checkBlock3ME);
			AST fixAST = AstUtil.getChild(unguardedWithFixAST, EvlParser.FIX);
			ModuleElement fixBlock3Me = module.adapt(fixAST, unguardedWithFixME);
			assertThat(fixBlock3Me, instanceOf(Fix.class));
			fixBlock3Me.setParent(unguardedWithFixME);
			unguardedWithFixME.getChildren().add(fixBlock3Me);
			// 	Fix's guard should not be traced
			ModuleElement fixGuardBlock3Me = module.adapt(AstUtil.getChild(fixAST, EvlParser.GUARD), fixBlock3Me);
			assertThat(fixGuardBlock3Me, instanceOf(ExecutableBlock.class));
			assertThat(fixGuardBlock3Me, not(instanceOf(TracedExecutableBlock.class)));
			fixGuardBlock3Me.setParent(fixBlock3Me);
			fixBlock3Me.getChildren().add(fixGuardBlock3Me);
			
			// CritiqueSimple
			AST critiqueSimpleAST = AstUtil.getChild(singleContextAST, EvlParser.CRITIQUE);
			ModuleElement critiqueSimpleME = module.adapt(critiqueSimpleAST, constraintContextME);
			assertThat(critiqueSimpleME, instanceOf(TracedConstraint.class));
			critiqueSimpleME.setParent(constraintContextME);
			constraintContextME.getChildren().add(critiqueSimpleME);
			ModuleElement checkBlock5ME = module.adapt(AstUtil.getChild(critiqueSimpleAST, EvlParser.CHECK), critiqueSimpleME);
			assertThat(checkBlock5ME, instanceOf(TracedExecutableBlock.class));
			checkBlock5ME.setParent(critiqueSimpleME);
			critiqueSimpleME.getChildren().add(checkBlock5ME);
			
		}
		
	}
	
	/**
	 * Test that the correct access traces are created
	 * @author Horacio Hoyos Rodriguez
	 *
	 */
	
	public static class ExecutionTests {
		
		private IncrementalEvlModule module;
		private File evlFile;

		@Before
		public void setup() throws URISyntaxException {
			module = new IncrementalEvlModule();
			evlFile = new File(IncrementalEvlModuleIntegrationTests.class.getResource("testExecution.evl").toURI());
		}
		
		@Test
		public void testAccessCreation() throws Exception {
			StringProperties properties = new StringProperties();
			properties.put(CsvModel.PROPERTY_NAME, "bank");
			properties.put(CsvModel.PROPERTY_HAS_KNOWN_HEADERS, "true");
			properties.put(CsvModel.PROPERTY_ID_FIELD, "iban");
			String path = IncrementalEvlModuleIntegrationTests.class.getResource("bankSmall.csv").getPath();
			properties.put(CsvModel.PROPERTY_FILE, path);
			CsvModelIncremental model = new CsvModelIncremental();
			model.load(properties, new IRelativePathResolver() {
				@Override
				public String resolve(String relativePath)
				{
					return relativePath;
				}
			});
			
			((TracedEvlContext) module.context).setTraceManager(new InMemoryEvlTraceManager());
			module.parse(evlFile);
			module.context.getModelRepository().addModel(model);
			module.execute();
			
			Set<IModuleElementTrace> executionTraces = ((TracedEvlContext) module.context).getTraceManager()
					.getExecutionTraceRepository().getAllExecutionTraces();
			List<IContextTrace> contextExecutionTraces = executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.collect(Collectors.toList());
			// We have two contexts, i.e. two traces per Row
			Collection<Map<String, Object>> modelRows = model.getAllOfType("Row");			
			assertThat("One ContextTrace per model element", contextExecutionTraces.size(), is(2));
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		    Date changeDate = sdf.parse("6/4/2017");
		    Iterator<IContextTrace> cIt = contextExecutionTraces.iterator();
		    
		    // FIXME This only asserts one context with one element... we should assert all?
		    IContextTrace contextTrace1 = cIt.next();
		    // Find the model element access of the trace
		    Stream<IElementAccess> elementAccesses = contextTrace1.accesses().get().stream()
		    			.filter(a -> a instanceof IElementAccess)
		    			.map(IElementAccess.class::cast);
		    assertThat("There should be 8 element accessess", elementAccesses.count(), is(8L));
			IElementAccess elementAccess = contextTrace1.accesses().get().stream()
	    			.filter(a -> a instanceof IElementAccess)
	    			.map(IElementAccess.class::cast)
		    		.findFirst()
		    		.get();
	    		// Find matching row
			Map<String, Object> currentRow = modelRows.stream()
					.filter(r -> r.get("iban").equals(elementAccess.element().get().getUri()))
					.findFirst()
					.get();
			assertThat("All traces have guard", contextTrace1.guard().get(), is(notNullValue()));
			if (changeDate.before(sdf.parse((String) currentRow.get("startDate")))) {
				assertThat(String.format("Row %s satisfies guard", currentRow.get("id")),
						contextTrace1.guard().get().getResult(),
						is(true));
				assertThat("Guard == two invariant traces", contextTrace1.constraints().get(), hasSize(2));
				// isInOverdraft
				IInvariantTrace isInOverdraft = contextTrace1.constraints().get().stream()
						.filter(c -> c.getName().equals("isInOverdraft"))
						.findFirst()
						.get();
				assertThat("isInOVerDraft has no satisfies access", isInOverdraft.satisfies().get(), is(nullValue()));
				//Remove the $ and convert to number
				float balance = Float.valueOf(((String)currentRow.get("balance")).substring(1));
				assertThat(isInOverdraft.guard().get(), is(nullValue()));
				
				Set<IAccess> accesses = isInOverdraft.check().get().accesses().get();
				
				assertThat(accesses, hasSize(1));
				IAccess balanceAccess = accesses.iterator().next();
				assertThat("Access is PropertyAccess", balanceAccess, instanceOf(IPropertyAccess.class));
				IPropertyAccess pa = (IPropertyAccess)balanceAccess;
				assertThat("Access is to 'balance' property.", pa.property().get().getName().equals("balance"));
				if (balance < 0) {
					IMessageTrace messageTrace = isInOverdraft.message().get();
					accesses = messageTrace.accesses().get();
					assertThat(accesses, hasSize(1));
					IAccess ibanAccess = accesses.iterator().next();
					assertThat("Access is PropertyAccess", ibanAccess, instanceOf(IPropertyAccess.class));
					pa = (IPropertyAccess)ibanAccess;
					assertThat("Access is to 'iban' property.", pa.property().get().getName().equals("iban"));
				}
				else {
					assertThat(isInOverdraft.message().get().accesses().get(), is(empty()));
				}
				// OverdraftCharges
				IInvariantTrace overdraftCharges = contextTrace1.constraints().get().stream()
						.filter(c -> c.getName().equals("OverdraftCharges"))
						.findFirst()
						.get();
				assertThat(overdraftCharges.guard().get(), is(notNullValue()));
				assertThat(overdraftCharges.guard().get().accesses().get(), is(empty()));
				assertThat("OverdraftCharges has satisfies access", overdraftCharges.satisfies().get().accesses().get(), hasSize(0));
				Queue<IInvariantTrace> satisfiedInvariants = overdraftCharges.satisfies().get().satisfiedInvariants().get();
				assertThat("OverdraftCharges satisfies one invariant", satisfiedInvariants, hasSize(1));
				IInvariantTrace satisfiedInvariantTrace = satisfiedInvariants.peek();
				assertThat("OverdraftCharges satisfies isInOverdraft", satisfiedInvariantTrace.getName(), is("isInOverdraft"));
				assertThat("OverdraftCharges satisfies isInOverdraft@one", overdraftCharges.satisfies().get().getAll(), is(false));
			
				if (balance < 0) {
					// Satisfies guard
					accesses = overdraftCharges.check().get().accesses().get();
					assertThat(accesses, hasSize(2));
					Set<String> allowedNames = new HashSet<>(Arrays.asList("hasOverdraft", "branch"));
					for (IAccess a : accesses) {
						assertThat("Access is PropertyAccess", a, instanceOf(IPropertyAccess.class));
						pa = (IPropertyAccess)a;
						assertThat("Access is to 'hasOverdraft'/'branch' property.", allowedNames, contains(pa.property().get().getName()));
					}
					if (Boolean.getBoolean((String) currentRow.get("hasOverdraft"))) {
						IMessageTrace messageTrace = overdraftCharges.message().get();
						accesses = messageTrace.accesses().get();
						assertThat(accesses, hasSize(1));
						IAccess ibanAccess = accesses.iterator().next();
						assertThat("Access is PropertyAccess", ibanAccess, instanceOf(IPropertyAccess.class));
						pa = (IPropertyAccess)ibanAccess;
						assertThat("Access is to 'iban' property.", pa.property().get().getName().equals("iban"));
					}
				}
				else {
					assertThat(overdraftCharges.message().get().accesses().get(), is(empty()));
				}
			}
			else {
				assertThat(String.format("Row %s does not satisfy context guard", currentRow.get("iban")),
						contextTrace1.guard().get().getResult(),
						is(false));
				// No access information
				List<IAccess> allAccesses = contextTrace1.constraints().get().stream()
						.flatMap(c -> c.accesses().get().stream())
						.collect(Collectors.toList());
				
				assertThat("No guard == no access information", allAccesses, is(empty()));
			}
		}
	}
	
	/**
	 * Test that the correct access traces are retrieved and the corresponding EVL blocks/rules executed.
	 * @author Horacio Hoyos Rodriguez
	 *
	 */
	public static class PostexecutionTests {
		
		private IncrementalEvlModule module;
		private File evlFile;
		private String csvFilePath;
		private File modelCopy;

		@Before
		public void setup() throws Exception {
			StringProperties properties = new StringProperties();
			properties.put(CsvModel.PROPERTY_NAME, "bank");
			properties.put(CsvModel.PROPERTY_HAS_KNOWN_HEADERS, "true");
			properties.put(CsvModel.PROPERTY_ID_FIELD, "iban");
			csvFilePath = IncrementalEvlModuleIntegrationTests.class.getResource("bankSmall.csv").getPath();
			properties.put(CsvModel.PROPERTY_FILE, csvFilePath);
			CsvModelIncremental model = new CsvModelIncremental();
			model.load(properties, new IRelativePathResolver() {
				@Override
				public String resolve(String relativePath)
				{
					return relativePath;
				}
			});
			module = new IncrementalEvlModule();
			evlFile = new File(IncrementalEvlModuleIntegrationTests.class.getResource("testExecution.evl").toURI());
			((TracedEvlContext) module.context).setTraceManager(new InMemoryEvlTraceManager());
			module.parse(evlFile);
			module.context.getModelRepository().addModel(model);
			// Make model copy
			modelCopy = saveModelCopy(csvFilePath);
		}
		
		@After
		public void teardown() throws Exception {
			restoreModel(modelCopy, csvFilePath);
		}
		
		@Test
		public void testOnCreate() throws Exception {
			
			module.setOnlineExecution(true);
			module.execute();
			// Save the previous state so we can compare changes
			// ContextTraces
			Set<IModuleElementTrace> executionTraces = ((TracedEvlContext) module.context).getTraceManager()
					.getExecutionTraceRepository().getAllExecutionTraces();
			
			long contextExecutionTraces = executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.count();
			// Unsatisfied constraints
			long unsatisfied = module.getContext().getUnsatisfiedConstraints().size();
			
			// Add a listener so we can synchronise the execution.
			BlockingQueue<ExecutionResult> results = new SynchronousQueue<ExecutionResult>();
			IExecutionListener assertExecutionListener = new IExecutionListener() {
				
				@Override
				public void finishedExecutingWithException(ModuleElement ast, EolRuntimeException exception, IEolContext context) {
					// TODO Implement Type1516897943540.finishedExecutingWithException
					throw new UnsupportedOperationException(
							"Unimplemented Method    Type1516897943540.finishedExecutingWithException invoked.");
				}
				
				@Override
				public void finishedExecuting(ModuleElement ast, Object result, IEolContext context) {
					try {
						results.put(new ExecutionResult(ast, result));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void aboutToExecute(ModuleElement ast, IEolContext context) {
					// We dont trace abouts
				}
			};
			module.getContext().getExecutorFactory().addExecutionListener(assertExecutionListener);
			
			// Insert Line 71 from test data
			String[] data = new String[]{"CZ63 7593 6158 0945 3366 6310","Bobrowice","bkennet1x@wiley.com",
					"5/16/2017","Brainsphere","false","$-3.35","Often"};
			Path csvPath = Paths.get(csvFilePath);
			CsvAddRowInjector injector = new CsvAddRowInjector(data, csvPath,
					CSVFormat.RFC4180.withDelimiter(',').withFirstRecordAsHeader(),
					CsvAppendMethod.RANDOM);
			ExecutorService executor = Executors.newSingleThreadExecutor();
	        Future<?> future = executor.submit(injector);
	        // Wait for changes in file to finish
	        while (future.isDone()) { }
	       	// Wait for CsvModelIncremental to pickup changes and send notifications
	        for (;;) {
		        	ExecutionResult r = results.poll(10, TimeUnit.SECONDS);
		        	//System.out.println(r);
		        	if (r == null) {
		        		break;
		        	}
	        }
	        
//	        executionTraces = ((TracedEvlContext) module.context).getTraceManager()
//					.getExecutionTraceRepository().getAllExecutionTraces();
			
			long contextExecutionTracesNew = executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.count();
			assertThat("A new row does not add new ContextTraces", contextExecutionTracesNew, is(contextExecutionTraces));
	        long unsatisfiedNew = module.getContext().getUnsatisfiedConstraints().size();
			assertThat("Added row should not break any constraints", unsatisfiedNew-unsatisfied, is(0L));
			for (IContextTrace ct : executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.collect(Collectors.toList())) {
				List<ElementAccess> eas = ct.accesses().get().stream()
						.filter(a -> a instanceof IElementAccess)
						.map(ElementAccess.class::cast)
						.collect(Collectors.toList());
				assertThat("Each contextTrace should have one more element access", eas.size(), is(9));
			}
			
			// Insert Line 264 from test data
			contextExecutionTraces = contextExecutionTracesNew;
			unsatisfied = unsatisfiedNew;
			String[] data2 = new String[]{"BE02 7334 0167 6391","Pelabuhanratu","rmaclaughlin7a@loc.gov",
					"11/21/2017","Buzzdog","true","$-28.03","Often"};
			injector = new CsvAddRowInjector(data2, csvPath,
					CSVFormat.RFC4180.withDelimiter(',').withFirstRecordAsHeader(),
					CsvAppendMethod.RANDOM);
			future = executor.submit(injector);
	        executor.shutdown();
	        // Wait for changes in file to finish
	        while (future.isDone()) { }
	        // Wait for evl to execute again
	        for (;;) {
		        	ExecutionResult r = results.poll(10, TimeUnit.SECONDS);
		        	if (r == null) {
		        		break;
		        	}
	        }
	        
//	        executionTraces = ((TracedEvlContext) module.context).getTraceManager()
//					.getExecutionTraceRepository().getAllExecutionTraces();
			
	        contextExecutionTracesNew = executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.count();
			assertThat("A new row does not add new ContextTraces", contextExecutionTracesNew, is(contextExecutionTraces));
	        unsatisfiedNew = module.getContext().getUnsatisfiedConstraints().size();
			assertThat("Added row should break two constraints", unsatisfiedNew-unsatisfied, is(2L));
		}
		
		@Ignore
		@Test
		public void testOnChange() throws Exception {
			module.setOnlineExecution(true);
			module.execute();
			// Save the previous state so we can compare changes
			// ContextTraces
			Set<IModuleElementTrace> executionTraces = ((TracedEvlContext) module.context).getTraceManager()
					.getExecutionTraceRepository().getAllExecutionTraces();
			long contextExecutionTraces = executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.count();
			// Unsatisfied constraints
			long unsatisfied = module.getContext().getUnsatisfiedConstraints().size();
			
			// Add a listener so we can synchronise the execution.
			BlockingQueue<ExecutionResult> results = new SynchronousQueue<ExecutionResult>();
			IExecutionListener assertExecutionListener = new IExecutionListener() {
				
				@Override
				public void finishedExecutingWithException(ModuleElement ast, EolRuntimeException exception, IEolContext context) {
					// TODO Implement Type1516897943540.finishedExecutingWithException
					throw new UnsupportedOperationException(
							"Unimplemented Method    Type1516897943540.finishedExecutingWithException invoked.");
				}
				
				@Override
				public void finishedExecuting(ModuleElement ast, Object result, IEolContext context) {
					try {
						results.put(new ExecutionResult(ast, result));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				@Override
				public void aboutToExecute(ModuleElement ast, IEolContext context) {
					// We dont trace abouts
				}
			};
			module.getContext().getExecutorFactory().addExecutionListener(assertExecutionListener);
			
			// Make a customer go into overdraft
			// PL04 6348 6856 3863 4689 7150 1771 had $85.98, take it to $-45.12
			Path csvPath = Paths.get(csvFilePath);
			CsvChangeInjector injector = new CsvChangeInjector(0, "PL04 6348 6856 3863 4689 7150 1771",
					6, "$-45.12",
					csvPath, CSVFormat.RFC4180.withDelimiter(',').withFirstRecordAsHeader());
			ExecutorService executor = Executors.newSingleThreadExecutor();
	        Future<?> future = executor.submit(injector);
	        // Wait for changes in file to finish
	        while (future.isDone()) { }
	        // Wait for CsvModelIncremental to pickup changes and send notifications
	        for (;;) {
		        	ExecutionResult r = results.poll(10, TimeUnit.SECONDS);
		        	//System.out.println(r);
		        	if (r == null) {
		        		break;
		        	}
	        }
//	        executionTraces = execution.executions().get();
	        
	        long contextExecutionTracesNew = executionTraces.stream()
					.filter(t -> t instanceof IContextTrace)
					.map(IContextTrace.class::cast)
					.count();
			assertThat("Change should not create new traces", contextExecutionTracesNew-contextExecutionTraces, is(0L));
	        long unsatisfiedNew = module.getContext().getUnsatisfiedConstraints().size();
			assertThat("Change breaks two constraints", unsatisfiedNew-unsatisfied, is(2L));
		}
		
		
		
		private class ExecutionResult {
			private final ModuleElement ast;
			private final Object result;
			public ExecutionResult(ModuleElement ast, Object result) {
				super();
				this.ast = ast;
				this.result = result;
			}
			@Override
			public String toString() {
				return String.format("Executed %s with result %s", ast.toString(), result); 
			}
			
		}
		
		private File saveModelCopy(String modelPath) throws IOException {
			File temp = File.createTempFile("temp-model", ".tmp");
    		//System.out.println("Temp file : " + temp.getAbsolutePath());
    		Files. copy(Paths.get(modelPath), temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
    		return temp;
	    }
		
		private void restoreModel(File temp, String modelPath) throws IOException {
			Files.copy(temp.toPath(), Paths.get(modelPath), StandardCopyOption.REPLACE_EXISTING);
		}
			
	}
	
}
