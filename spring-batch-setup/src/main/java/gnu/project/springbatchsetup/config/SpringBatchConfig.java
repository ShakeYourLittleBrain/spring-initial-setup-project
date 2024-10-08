package gnu.project.springbatchsetup.config;

import gnu.project.springbatchsetup.entity.Customer;
import gnu.project.springbatchsetup.listener.StepSkipListener;
import gnu.project.springbatchsetup.repository.CustomerRepository;
import org.springframework.batch.core.Job;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.io.File;

@Configuration
@EnableBatchProcessing
//@AllArgsConstructor
public class SpringBatchConfig {


    private final CustomerRepository customerRepository;
    private final CustomerItemWriter customerItemWriter;

    public SpringBatchConfig( CustomerRepository customerRepository, CustomerItemWriter customerItemWriter) {
        this.customerRepository = customerRepository;
        this.customerItemWriter = customerItemWriter;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Customer> itemReader(@Value("#{jobParameters[fullPathFileName]}") String pathToFIle) {
        FlatFileItemReader<Customer> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource(new File(pathToFIle)));
        flatFileItemReader.setName("CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    private LineMapper<Customer> lineMapper() {
        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "contactNo", "country", "dob", "age");

        BeanWrapperFieldSetMapper<Customer> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Customer.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public CustomerProcessor processor() {
        return new CustomerProcessor();
    }

    @Bean
    public RepositoryItemWriter<Customer> writer() {
        RepositoryItemWriter<Customer> writer = new RepositoryItemWriter<>();
        writer.setRepository(customerRepository);
        writer.setMethodName("save");
        return writer;
    }


    @Bean
    public Step step1(JobRepository jobRepository,
                      DataSourceTransactionManager transactionManager, FlatFileItemReader<Customer> itemReader) {
        return new StepBuilder("jobStep", jobRepository).<Customer, Customer>chunk(10, transactionManager)
                .reader(itemReader)
                .processor(processor())
                .writer(customerItemWriter)
                .faultTolerant()
                .listener(skipListener())
                .skipPolicy(skipPolicy())
                .taskExecutor(taskExecutor())
                .build();
    }


    @Bean
    public Job runJob(JobRepository jobRepository,
                      Step step1) {
        return new JobBuilder("job", jobRepository).start(step1).build();
    }


    @Bean
    public SkipPolicy skipPolicy() {
        return new ExceptionSkipPolicy();
    }

    @Bean
    public SkipListener skipListener() {
        return new StepSkipListener();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(10);
        return taskExecutor;
    }


}
