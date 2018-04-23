app.controller("invoiceController", function($scope, $http, $req, $q, $filter, $window, $timeout, $translate) {
	$scope.ui = ts.ui;
	
	$scope.ui.ready(function() {
		
		$scope.topbar = ts.ui.get("#home-topbar");
		$scope.invoiceTable = ts.ui.get("#invoice-table");
		$scope.pop = ts.ui.Notification;
		$scope.showTab = 0;
		$scope.popup = ts.ui.Notification;
		$scope.stag = "inbox";
		$scope.minIssueDate = null;
		$scope.maxIssueDate = null;
		$scope.createdAfter = null;
		$scope.createdBefore = null;
		$scope.states = [];
		$scope.limit = 10;
		$scope.page = 0;
		$scope.invoices = [];
		$scope.selectedRows = [];
		
		$q.all([
		    $translate(["Tab.Invoice", 
		                "Table.ID", "Table.ReceiverCompany", "Table.SenderCompany", "Table.Description", "Table.Total", "Table.Currency", "Table.IssueDate", "Table.State",
		                "Stag.Inbox", "Stag.Outbox",
		                "ProcessState.Pending", "ProcessState.Invoiced", "ProcessState.Overdue", "ProcessState.Accepted", "ProcessState.Paid", "ProcessState.Rejected", "ProcessState.Disputed",
		                "Index.Download"])        
		])
		.then(function(response) {
			var locale = response[0];
			
			$scope.topbar.tabs([{
				label: locale["Tab.Invoice"],
				id: "tab0",
				onselect: function() {
					$scope.showTab = 0;
					$scope.$apply();
					scrollTo(0, 0);
				}
			}]);
			
			$scope.stagOptions = [
			    { value : "inbox", text : locale["Stag.Inbox"] },
			    { value : "outbox", text : locale["Stag.Outbox"] }
			];
			$scope.stateOptions = [
			    { value : "PENDING", text : locale["ProcessState.Pending"] },
			    { value : "INVOICED", text : locale["ProcessState.Invoiced"] },
			    { value : "OVERDUE", text : locale["ProcessState.Overdue"] },
			    { value : "ACCEPTED", text : locale["ProcessState.Accepted"] },
			    { value : "PAID", text : locale["ProcessState.Paid"] },
			    { value : "REJECTED", text : locale["ProcessState.Rejected"] },
			    { value : "DISPUTED", text : locale["ProcessState.Disputed"] }
			];
			
			$scope.invoiceTable.cols([{
				label: locale["Table.ID"]
			},{
				label: locale["Table.ReceiverCompany"], flex: 2
			}, {
				label: locale["Table.SenderCompany"], flex: 2
			}, {
				label: locale["Table.Description"], flex: 3
			}, {
				label: locale["Table.Total"], type: "ts-number"
			}, {
				label: locale["Table.Currency"]
			}, {
				label: locale["Table.IssueDate"]
			}, {
				label: locale["Table.State"]
			}])
			.sortable(function(index, ascending) {
				$scope.invoiceTable.sort(index, ascending);
			})
			.selectable(function(selected, unselected) {
				selectedRows = selected;
			})
			.buttons([
				{label: locale["Index.Download"], type='ts-primary', onclick: () => {
					$scope.download();
				}}
			]) 
			.max(10).sort(0, true);
			
			$scope.submitForm = function() {
				var predicates = buildPredicates();
				
				$req.searchInvoices(predicates)
				.then(function(response) {
					console.log(response);
					var contentType = response.headers('Content-Type');
					console.log('Content-Type: ' + contentType);
					if(response.status == 200 && contentType.indexOf('application/json') >= 0) {
						if(response.data.length == 0) {
							$scope.pop.info("No records found match the criteria.");
						}
						$scope.invoices = response.data;
						$scope.selectedRows = [];
						populateInvoiceTable();
					} else {
						if(response.status != 200) {
							$scope.pop.error('Failed to get response. HTTP Status: ' + response.status);
						} else {
							$scope.pop.error('Failed to get JSON response. Content-Type: ' + contentType);
						}
					}
					
				}, function(error) {
					console.log(error);
					$scope.pop.error(error.data.message);
				});
			}
			
			$scope.clearForm = function() {
				$scope.stag = "inbox";
				$scope.minIssueDate = null;
				$scope.maxIssueDate = null;
				$scope.createdAfter = null;
				$scope.createdBefore = null;
				$scope.states = [];
				$scope.limit = 10;
				$scope.page = 0;
				$scope.invoices = [];
				$scope.selectedRows = [];
				$scope.invoiceTable.rows([]);
				$scope.$apply();
			};
			
			$scope.download = function() {
				$q.all([
				        $req.downloadInvoice("1")
				])
				.then(function(response) {
					console.log(response.data);			
					if(response.status == 200) {
						
					} else {
						$scope.pop.error(response.statusText);
					}
				}, function(error) {
					console.log(error);
					$scope.pop.error(error.data.message);
				});
			};
			
		});
		
		$scope.test = function() {
			$req.test()
			.then(function(response) {
				console.log(response);
			}, function(error) {
				console.log(error);
			});
		}
		
		function buildPredicates() {
			
			var predicates = { 
					limit: $scope.limit,
					page: $scope.page
			};
			
			if($scope.stag) {
				predicates.stag = $scope.stag;
			}
			if($scope.minIssueDate) {
				predicates.minissuedate = $scope.minIssueDate;
			}
			if($scope.maxIssueDate) {
				predicates.maxissuedate = $scope.maxIssueDate;
			}
			if($scope.createdBefore) {
				predicates.createdBefore = $scope.createdBefore;
			}
			if($scope.createdAfter) {
				predicates.createdAfter = $scope.createdAfter;
			}
			var vals = [];
			for(var i = 0; i < $scope.states.length; i++) {
				vals.push($scope.states[i]);
			}
			if(vals.length > 0) {
				predicates.processState = vals;
			}
			
			return predicates;
		}
		
		function populateInvoiceTable() {
			var rows = [];
			for(var i = 0; i < $scope.invoices.length; i++) {
				var invoice = $scope.invoices[i];
				rows.push([ invoice.id, invoice.receiverCompanyName, invoice.senderCompanyName, invoice.description, invoice.total, invoice.currency, invoice.issueDate, invoice.state]);
			}
			$scope.invoiceTable.rows(rows);
		}
		
	});
});