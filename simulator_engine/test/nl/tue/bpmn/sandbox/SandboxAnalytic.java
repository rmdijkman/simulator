package nl.tue.bpmn.sandbox;

import nl.tue.util.QueueingFormulas;

public class SandboxAnalytic {

	public static void analyticResults(){
		double mu_A = 2;
		double mu_B = 2;
		
		double lambda_A = 1;
		double lambda_B = 1;

		double c = 2;
		
		double lambda = lambda_A + lambda_B;
		double EB = (lambda_A/(lambda_A+lambda_B))*QueueingFormulas.EB(mu_A)+(lambda_B/(lambda_A+lambda_B))*QueueingFormulas.EB(mu_B);
		double EB2 = (lambda_A/(lambda_A+lambda_B))*QueueingFormulas.EB2(mu_A)+(lambda_B/(lambda_A+lambda_B))*QueueingFormulas.EB2(mu_B);
		double rho = QueueingFormulas.rho(lambda, 1.0/EB, c);
		
		double EW = QueueingFormulas.EWMMc(EB, EB2, rho, c);
		double Lq = EW*lambda;
		
		System.out.println("EW=" + EW);
		System.out.println("Lq=" + Lq);
		System.out.println("rho=" + rho);
		System.out.println("ES=" + EW+EB);		
	}

	public static void main(String[] args) {
		analyticResults();
	}

}
