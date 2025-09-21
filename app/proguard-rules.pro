 (cd "$(git rev-parse --show-toplevel)" && git apply --3way <<'EOF' 
diff --git a//dev/null b/app/proguard-rules.pro
index 0000000000000000000000000000000000000000..fb164d6662744925ac5147eae0a5b6b763b171b4 100644
--- a//dev/null
+++ b/app/proguard-rules.pro
@@ -0,0 +1 @@
+# Add project specific ProGuard rules here.
 
EOF
)
