             .selected-products-table tbody tr:hover {
                 background-color: #e8f5e8;
                 transform: scale(1.01);
             }
             
             /* CSS cho validation */
             .form-control.is-invalid {
                 border-color: #dc3545;
                 box-shadow: 0 0 0 0.2rem rgba(220, 53, 69, 0.25);
             }
             
             .form-control.is-valid {
                 border-color: #198754;
                 box-shadow: 0 0 0 0.2rem rgba(25, 135, 84, 0.25);
             }
             
             .invalid-feedback {
                 display: block;
                 color: #dc3545;
                 font-size: 0.875rem;
                 margin-top: 0.25rem;
             }
             
             /* CSS cho product items */
             .product-item {
                 border: 2px solid #e9ecef !important;
                 transition: all 0.3s ease;
                 cursor: pointer;
                 position: relative;
                 overflow: hidden;
             }
             
             .product-item::before {
                 content: '';
                 position: absolute;
                 top: 0;
                 left: -100%;
                 width: 100%;
                 height: 100%;
                 background: linear-gradient(90deg, transparent, rgba(0,123,255,0.1), transparent);
                 transition: left 0.5s;
             }
             
             .product-item:hover::before {
                 left: 100%;
             }
             
             .product-item:hover {
                 background-color: #f8f9fa;
                 border-color: #007bff !important;
                 transform: translateY(-2px);
                 box-shadow: 0 4px 8px rgba(0,123,255,0.1);
             }
             
             /* CSS cho add button */
             .add-to-stock-btn:disabled {
                 opacity: 0.6;
                 cursor: not-allowed;
             }
             
             .add-to-stock-btn.btn-success {
                 background-color: #198754 !important;
                 border-color: #198754 !important;
                 color: white !important;
             }
         </style>

