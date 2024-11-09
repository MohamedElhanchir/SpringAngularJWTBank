package elhanchir.mohamed.ebankingbackend;

import elhanchir.mohamed.ebankingbackend.dtos.BankAccountDTO;
import elhanchir.mohamed.ebankingbackend.dtos.CurrentBankAccountDTO;
import elhanchir.mohamed.ebankingbackend.dtos.CustomerDTO;
import elhanchir.mohamed.ebankingbackend.dtos.SavingBankAccountDTO;
import elhanchir.mohamed.ebankingbackend.entities.AccountOperation;
import elhanchir.mohamed.ebankingbackend.entities.CurrentAccount;
import elhanchir.mohamed.ebankingbackend.entities.Customer;
import elhanchir.mohamed.ebankingbackend.entities.SavingAccount;
import elhanchir.mohamed.ebankingbackend.enums.AccountStatus;
import elhanchir.mohamed.ebankingbackend.enums.OperationType;
import elhanchir.mohamed.ebankingbackend.exceptions.CustomerNotFoundException;
import elhanchir.mohamed.ebankingbackend.repositories.AccountOperationRepository;
import elhanchir.mohamed.ebankingbackend.sec.entities.AppRole;
import elhanchir.mohamed.ebankingbackend.sec.entities.AppUser;
import elhanchir.mohamed.ebankingbackend.sec.services.AccountService;
import elhanchir.mohamed.ebankingbackend.services.BankAccountService;
import elhanchir.mohamed.ebankingbackend.repositories.BankAccountRepository;
import elhanchir.mohamed.ebankingbackend.repositories.CustomerRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {



    public static void main(String[] args) {

        SpringApplication.run(EbankingBackendApplication.class, args);

    }




    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
        return args -> {

           Stream.of("Hassan","Imane","Mohamed").forEach(name->{
               CustomerDTO customer=new CustomerDTO();
               customer.setName(name);
               customer.setEmail(name+"@gmail.com");
               bankAccountService.saveCustomer(customer);
           });
           bankAccountService.listCustomers().forEach(customer->{
               try {
                   bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
                   bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());

               } catch (CustomerNotFoundException e) {
                   e.printStackTrace();
               }
           });
            List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
            for (BankAccountDTO bankAccount:bankAccounts){
                for (int i = 0; i <10 ; i++) {
                    String accountId;
                    if(bankAccount instanceof SavingBankAccountDTO){
                        accountId=((SavingBankAccountDTO) bankAccount).getId();
                    } else{
                        accountId=((CurrentBankAccountDTO) bankAccount).getId();
                    }
                    bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
                    bankAccountService.debit(accountId,1000+Math.random()*9000,"Debit");
                }
            }
        };
    }

    //@Bean
    CommandLineRunner start(CustomerRepository customerRepository,
                            BankAccountRepository bankAccountRepository,
                            AccountOperationRepository accountOperationRepository){
        return args -> {
            Stream.of("Hassan","Yassine","Aicha").forEach(name->{
                Customer customer=new Customer();
                customer.setName(name);
                customer.setEmail(name+"@gmail.com");
                customerRepository.save(customer);
            });
            customerRepository.findAll().forEach(cust->{
                CurrentAccount currentAccount=new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random()*90000);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(cust);
                currentAccount.setOverDraft(9000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount=new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random()*90000);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(cust);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);

            });
            bankAccountRepository.findAll().forEach(acc->{
                for (int i = 0; i <10 ; i++) {
                    AccountOperation accountOperation=new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random()*12000);
                    accountOperation.setType(Math.random()>0.5? OperationType.DEBIT: OperationType.CREDIT);
                    accountOperation.setBankAccount(acc);
                    accountOperationRepository.save(accountOperation);
                }

            });
        };

    }

    @Bean
    CommandLineRunner start2(AccountService accountService) {
        return args -> {
            accountService.addNewRole(new AppRole(null, "USER"));
            accountService.addNewRole(new AppRole(null, "ADMIN"));


            accountService.addNewUser(new AppUser(null, "user1", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "user2", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "user3", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "user4", "1234", new ArrayList<>()));
            accountService.addNewUser(new AppUser(null, "admin1", "1234", new ArrayList<>()));

            accountService.addRoleToUser("user1", "USER");
            accountService.addRoleToUser("user2", "USER");
            accountService.addRoleToUser("user3", "USER");
            accountService.addRoleToUser("user4", "USER");
            accountService.addRoleToUser("admin1", "ADMIN");
            accountService.addRoleToUser("admin1", "USER");

        };
    }

}
