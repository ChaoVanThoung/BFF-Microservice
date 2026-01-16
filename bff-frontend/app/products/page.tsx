"use client";

import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ProductDialog } from "@/components/product-dialog";
import { Plus, LogOut, Loader2, AlertCircle, Package } from "lucide-react";
import { getCurrentUser, getProducts, createProduct, logout } from "@/lib/api-client";

interface UserInfo {
  username?: string;
  email?: string;
  name?: string;
}

interface Product {
  id: string;
  title: string;
  price: number;
}

export default function ProductsPage() {
  const [user, setUser] = useState<UserInfo | null>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    setError(null);

    const [userResult, productsResult] = await Promise.all([
      getCurrentUser(),
      getProducts()
    ]);

    if (!userResult.success) {
      setError(userResult.error || "Failed to load user");
      setLoading(false);
      return;
    }

    if (!productsResult.success) {
      setError(productsResult.error || "Failed to load products");
    }

    setUser(userResult.data || null);
    setProducts(productsResult.data || []);
    setLoading(false);
  };

  const handleCreateProduct = async (title: string, price: string) => {
    const result = await createProduct(title, parseFloat(price));
    
    if (result.success) {
      await loadData(); // Reload products
    } else {
      setError(result.error || "Failed to create product");
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center space-y-4">
          <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <Package className="h-6 w-6 text-primary" />
            <h1 className="text-xl font-bold">Product Management</h1>
          </div>
          
          <div className="flex items-center gap-4">
            {user && (
              <div className="text-sm">
                <span className="text-muted-foreground">Welcome, </span>
                <span className="font-medium">{user.name || user.username}</span>
              </div>
            )}
            <Button variant="outline" size="sm" onClick={logout}>
              <LogOut className="h-4 w-4 mr-2" />
              Logout
            </Button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {error && (
          <div className="mb-6 p-4 bg-red-50 dark:bg-red-950 border border-red-200 dark:border-red-800 rounded-lg flex gap-3">
            <AlertCircle className="h-5 w-5 text-red-600 dark:text-red-400 flex-shrink-0 mt-0.5" />
            <div>
              <p className="font-medium text-red-600 dark:text-red-400">Error</p>
              <p className="text-sm text-red-600 dark:text-red-400">{error}</p>
            </div>
          </div>
        )}

        <div className="flex justify-between items-center mb-6">
          <div>
            <h2 className="text-2xl font-bold">Products</h2>
            <p className="text-muted-foreground">Manage your product inventory</p>
          </div>
          <Button onClick={() => setDialogOpen(true)}>
            <Plus className="h-4 w-4 mr-2" />
            Add Product
          </Button>
        </div>

        {/* Products Grid */}
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
          {products.length === 0 ? (
            <div className="col-span-full text-center py-12">
              <Package className="h-12 w-12 text-muted-foreground mx-auto mb-4" />
              <p className="text-muted-foreground">No products yet. Create your first product!</p>
            </div>
          ) : (
            products.map((product) => (
              <Card key={product.id}>
                <CardHeader>
                  <CardTitle className="text-lg">{product.title}</CardTitle>
                </CardHeader>
                <CardContent>
                  <p className="text-2xl font-bold text-primary">
                    ${product.price.toFixed(2)}
                  </p>
                </CardContent>
              </Card>
            ))
          )}
        </div>
      </main>

      {/* Product Dialog */}
      <ProductDialog
        open={dialogOpen}
        onOpenChange={setDialogOpen}
        onSubmit={handleCreateProduct}
      />
    </div>
  );
}