package gnu.project.springbatchsetup.config;

import gnu.project.springbatchsetup.repository.CustomerRepository;
import gnu.project.springbatchsetup.entity.Customer;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;


@Component
public class CustomerItemWriter implements ItemWriter<Customer> {

    private final CustomerRepository repository;

    public CustomerItemWriter(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        System.out.println("Writer Thread "+Thread.currentThread().getName());
        repository.saveAll(chunk);
    }
}
