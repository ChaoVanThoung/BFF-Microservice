"use client";

import { ArrowRight, Star } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";

export default function Home() {
  return (
    <div className="min-h-screen bg-background text-foreground">
      {/* Navigation */}
      <nav className="sticky top-0 z-50 bg-background/80 backdrop-blur-md border-b border-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="text-2xl font-bold">Premium</div>
          <div className="hidden md:flex gap-8 items-center">
            <a
              href="#features"
              className="text-muted-foreground hover:text-foreground transition"
            >
              Features
            </a>
            <a
              href="#pricing"
              className="text-muted-foreground hover:text-foreground transition"
            >
              Pricing
            </a>
            <a
              href="#testimonials"
              className="text-muted-foreground hover:text-foreground transition"
            >
              Testimonials
            </a>
          </div>
          <Link href="/login">
            <Button
              variant="outline"
              size="sm"
              className="ml-auto mr-2 bg-transparent"
            >
              Log in
            </Button>
          </Link>
          <Button size="sm" className="bg-primary">
            Sign up
          </Button>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-32">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div className="space-y-8">
              <div className="space-y-4">
                <h1 className="text-5xl md:text-6xl font-bold leading-tight text-balance">
                  Experience the future of shopping
                </h1>
                <p className="text-lg md:text-xl text-muted-foreground leading-relaxed">
                  Join thousands of customers discovering premium products
                  curated just for you. Seamless checkout, exceptional quality,
                  delivered fast.
                </p>
              </div>

              <div className="space-y-4">
                <form
                  className="space-y-3"
                  onSubmit={(e) => e.preventDefault()}
                >
                  <Input
                    type="email"
                    placeholder="Enter your email"
                    className="h-12 text-base"
                  />
                  <Button
                    size="lg"
                    className="w-full bg-primary hover:bg-primary/90"
                  >
                    Get Early Access
                    <ArrowRight className="ml-2 h-4 w-4" />
                  </Button>
                </form>
                <p className="text-sm text-muted-foreground">
                  ✓ No credit card required · Free access · Unsubscribe anytime
                </p>
              </div>
            </div>

            <div className="relative h-96 md:h-full bg-gradient-to-br from-accent/20 to-accent/5 rounded-2xl flex items-center justify-center">
              <div className="absolute inset-0 overflow-hidden rounded-2xl">
                <div className="absolute top-1/4 left-1/4 w-48 h-48 bg-accent/30 rounded-full blur-3xl"></div>
                <div className="absolute bottom-1/4 right-1/4 w-48 h-48 bg-primary/20 rounded-full blur-3xl"></div>
              </div>
              <div className="relative z-10 text-center">
                <div className="text-6xl mb-4">📦</div>
                <p className="text-muted-foreground">
                  Premium Product Collection
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Trust Section */}
      <section className="bg-muted/30 border-y border-border">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
          <p className="text-center text-sm font-semibold text-muted-foreground mb-8">
            TRUSTED BY LEADING BRANDS
          </p>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
            {["Nike", "Adidas", "Puma", "Asics"].map((brand) => (
              <div key={brand} className="text-muted-foreground font-semibold">
                {brand}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-4">
              Why choose us?
            </h2>
            <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
              Everything you need for an exceptional shopping experience
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {[
              {
                title: "Curated Selection",
                description:
                  "Handpicked products from premium brands, carefully selected for quality and style.",
                icon: "⭐",
              },
              {
                title: "Fast Shipping",
                description:
                  "Get your orders delivered to your door in 2-3 business days, guaranteed.",
                icon: "🚚",
              },
              {
                title: "Customer Support",
                description:
                  "24/7 dedicated support team ready to help with any questions or concerns.",
                icon: "💬",
              },
              {
                title: "Easy Returns",
                description:
                  "30-day hassle-free returns. If you're not satisfied, we make it right.",
                icon: "↩️",
              },
              {
                title: "Secure Checkout",
                description:
                  "Bank-level encryption protects your payment information every time.",
                icon: "🔒",
              },
              {
                title: "Exclusive Deals",
                description:
                  "Early access to sales and exclusive discounts for our members.",
                icon: "🎁",
              },
            ].map((feature, i) => (
              <div
                key={i}
                className="p-8 rounded-lg border border-border hover:border-primary/50 hover:bg-primary/5 transition"
              >
                <div className="text-4xl mb-4">{feature.icon}</div>
                <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                <p className="text-muted-foreground">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="bg-primary text-primary-foreground py-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-4 gap-8 text-center">
            <div>
              <div className="text-4xl md:text-5xl font-bold mb-2">50K+</div>
              <p className="text-primary-foreground/80">Active Members</p>
            </div>
            <div>
              <div className="text-4xl md:text-5xl font-bold mb-2">98%</div>
              <p className="text-primary-foreground/80">Satisfaction Rate</p>
            </div>
            <div>
              <div className="text-4xl md:text-5xl font-bold mb-2">500K+</div>
              <p className="text-primary-foreground/80">Orders Delivered</p>
            </div>
            <div>
              <div className="text-4xl md:text-5xl font-bold mb-2">24/7</div>
              <p className="text-primary-foreground/80">Customer Support</p>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section id="testimonials" className="py-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-16">
            <h2 className="text-4xl md:text-5xl font-bold mb-4">
              What our customers say
            </h2>
            <p className="text-lg text-muted-foreground">
              Join thousands of satisfied shoppers
            </p>
          </div>

          <div className="grid md:grid-cols-3 gap-8">
            {[
              {
                name: "Sarah Chen",
                role: "Fashion Enthusiast",
                content:
                  "The quality of products exceeded my expectations. Fast shipping and excellent customer service!",
              },
              {
                name: "Marcus Johnson",
                role: "Regular Customer",
                content:
                  "Love the curated selection. It's like having a personal shopper. Highly recommend!",
              },
              {
                name: "Emma Rodriguez",
                role: "Verified Buyer",
                content:
                  "Best online shopping experience I've had. The returns process is so easy and hassle-free.",
              },
            ].map((testimonial, i) => (
              <div
                key={i}
                className="p-8 rounded-lg border border-border bg-card"
              >
                <div className="flex gap-1 mb-4">
                  {[...Array(5)].map((_, j) => (
                    <Star
                      key={j}
                      size={16}
                      className="fill-accent text-accent"
                    />
                  ))}
                </div>
                <p className="text-foreground mb-6 leading-relaxed">
                  "{testimonial.content}"
                </p>
                <div>
                  <p className="font-semibold text-foreground">
                    {testimonial.name}
                  </p>
                  <p className="text-sm text-muted-foreground">
                    {testimonial.role}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-muted/40 py-20 border-t border-border">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h2 className="text-4xl md:text-5xl font-bold mb-6">
            Ready to get started?
          </h2>
          <p className="text-lg text-muted-foreground mb-8">
            Join our community of happy shoppers today
          </p>
          <form
            className="flex flex-col sm:flex-row gap-3 max-w-md mx-auto"
            onSubmit={(e) => e.preventDefault()}
          >
            <Input
              type="email"
              placeholder="Your email address"
              className="flex-1 h-12 text-base"
            />
            <Button
              size="lg"
              className="bg-primary hover:bg-primary/90 whitespace-nowrap"
            >
              Sign me up
              <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </form>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-background border-t border-border py-12">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid md:grid-cols-4 gap-8 mb-8">
            <div>
              <h4 className="font-semibold mb-4">Product</h4>
              <ul className="space-y-2 text-muted-foreground text-sm">
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Features
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Pricing
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Security
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Company</h4>
              <ul className="space-y-2 text-muted-foreground text-sm">
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    About
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Blog
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Careers
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Legal</h4>
              <ul className="space-y-2 text-muted-foreground text-sm">
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Privacy
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Terms
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Contact
                  </a>
                </li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold mb-4">Follow us</h4>
              <ul className="space-y-2 text-muted-foreground text-sm">
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Twitter
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    Instagram
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-foreground transition">
                    LinkedIn
                  </a>
                </li>
              </ul>
            </div>
          </div>
          <div className="border-t border-border pt-8 text-center text-muted-foreground text-sm">
            <p>&copy; 2026 Premium. All rights reserved.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}
