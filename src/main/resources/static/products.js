const userId = 1;

async function loadProducts() {

    try {

        const response = await fetch("/api/products");

        if (!response.ok) {
            throw new Error("Products load nahi ho rahe");
        }

        const products = await response.json();

        const productList = document.getElementById("productList");

        productList.innerHTML = "";

        products.forEach(product => {

            productList.innerHTML += `
                <div class="col-md-4 mb-4">

                    <div class="card h-100 shadow-sm">

                        <img src="${product.imageUrl || 'https://via.placeholder.com/300'}"
                             class="card-img-top"
                             style="height:250px; object-fit:cover;">

                        <div class="card-body">

                            <h5 class="card-title">
                                ${product.name}
                            </h5>

                            <p class="card-text">
                                ${product.description || ""}
                            </p>

                            <h5>
                                ₹${product.price}
                            </h5>

                            <p>
                                Stock: ${product.quantity}
                            </p>

                            <button
                                class="btn btn-primary"
                                onclick="addToCart(${product.id})">
                                Add to Cart
                            </button>

                        </div>

                    </div>

                </div>
            `;
        });

    } catch (error) {

        console.error(error);
        alert("Products load karne mein problem aa rahi hai.");

    }
}


async function addToCart(productId) {

    try {

        const response = await fetch(
            `/api/cart/add?userId=${userId}&productId=${productId}&quantity=1`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error("Cart mein add nahi hua");
        }

        const cart = await response.json();

        alert("Product Cart mein add ho gaya! 🛒");

        console.log("Cart:", cart);

    } catch (error) {

        console.error(error);
        alert("Product Cart mein add nahi ho paya.");

    }
}


loadProducts();